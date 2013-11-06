#!/usr/bin/python
class ChatBot():
    def __init__(self):
        
        # 
        # varialbes and constants
        #
        #
        self.debug = True

        self.MAX_DISTANCE = 1000    # this could be so because the max distance
        self.MSG_LAT_LON_NOT_AVAILABLE = -10         
        self.MSG_CON_PIN_LOCATION_NEEDED = -50
        self.MSG_GEN_NO_LOCATION = -30
        self.MSG_REFER  = -52
        self.MSG_GEN_MENTION = -53
        self.MSG_NUM_ISS_N_LOC = -54
        self.MSG_NUM_ISS_N_LOC_1 = -55
        
        self.website_url = "empanada.cs.fiu.edu"
        self.mentions = []
        self.keys = []
        self.synonyms = []
        self.radius = "4"
        self.current_filter = "" # current filter
        self.current_mention = {} # current mention
        self.current_synonym = "" # current synonym
        self.question = False
        self.filter = self.get_filters()
        self.error = self.get_errors()
        self.keyword = '@empanada305'
        self.twitter_username = "empanada305" # hardcoded not a good idea                
        
        # Messages (Strings)
        self.website_address = "http://empanada.cs.fiu.edu"                
        

        

    # read filters from json in 
    # http://empanada.cs.fiu.edu/json/filters.json
    #####
    def get_filters(self):
        # importing json for parsing purposes
        # importing httplib to make http request
        #
        import json
        import httplib        
        # json_filters will store the filters in a json format
        json_filters = {}        
        # creating connection to the website
        # and makking a get request
        #
        conn = httplib.HTTPConnection(self.website_url)
        conn.request("GET", "/json/filters.json")        
        # getting a reponse message
        #
        response = conn.getresponse()
        if (response.reason == "OK"):
            data = response.read()
            json_filters = json.loads(data)
            
        conn.close()
        return json_filters
    



    # finds out whether the question needs the closest location
    def analyze_question(self, text):        
        # from here on we check question like
        #     is there ...... 
        #     are there .....
        #     what .. [close, closest, closer, near, around, nearby.....] .....
        #     where .. [get, find, acquire, ......] .....
        ##
        question = { "is": ["there"],
                     "are": ["there"],
                     "what": ["close", "near", "around", "nearby", "adjacent", "not far"],
                     "where": ["get", "find", "acquire", "obtain", "close", "buy", "purchase", "are", "is"]
                    }
        # iterate through the keys
        # if the key is found then we iterate
        # through the values
        #
        for key in question:
            if (key.upper() in text.upper()):

                for next in question[key]:
                    if (next.upper() in text.upper()):
                        # return code for location need
                        # this is used to find a pin location
                        # of the closes issue
                        return self.MSG_CON_PIN_LOCATION_NEEDED                    
        # this is the check for simple command like sentences like
        # water status?
        # find gas?
        ###
        sentence = str(text.upper()).replace(" ", "")
        before_keyword = ["find", "closest", "nearest", "get", "search"]
        for bf in before_keyword:
            if (str.upper(bf) + self.current_synonym.upper()) in sentence:
                return self.MSG_NUM_ISS_N_LOC + self.random(-1,0)
        # this is the check for simple command like sentences like
        # gas status?
        # or add more keywords after status for more sentence variety
        ###
        after_keyword = ["status"]
        for af in after_keyword:
            if (self.current_synonym.upper() + str.upper(af)) in sentence:
                return self.MSG_NUM_ISS_N_LOC + self.random(-1,0) 
        # return value
        ##        
        return 0 # this could be -1 for invalid question



    # adds incoming tweets to metions if the contain the self.keyword phrase
    # it won't get added if the tweet came from the same username
    # right now i'm just checking that the tweet mentions @empanada305
    # we can remove this later to analyze all incoming tweets
    #
    def add_to_mention(self, tweet):        
        # if tweet is from the same username then we ignore the tweet
        if ( tweet['user']['screen_name'] == self.twitter_username ): 
            # print debug message if debug is turned on
            if self.debug: print "DEBUG - " + tweet['user']['screen_name'] + " came from same user"
            return -1 # tweet won't get a reply        
        # for key in self.keyword: 
        # this could be remove or we could add more keywords
        # if the tweet mentions @empanada305 then we move on to find the filter in the text
        if ( str.upper(self.keyword) in tweet['text'].upper() ):            
            # iterate through all the filters
            for filter_key in self.filter:
                # here we added another for loop to iterate
                # for synonyms for example fuel = gas = diesel
                # for synonym in self.filter[filter_key]['synonyms']
                ####
                for syno in self.filter[filter_key]['synonyms']:                    
                    # Check whether the one of the synonyms 
                    # related to the keyword is used in the tweet 
                    #####
                    if ( syno.upper() in tweet['text'].upper() ):                        
                        if self.debug: print "DEBUG - added the tweet to the queue"
                        self.mentions.append(tweet)
                        self.keys.append(filter_key)
                        # here add another variable for synonym used
                        # self.synonym_used.append(synonym)
                        # added the synonym being used to detect the question
                        # could be different from category
                        #####
                        self.synonyms.append(syno)
                        # find out whether the question needs a close location                                            
                        return 0; # tweet had the self.keyword in it and one of the synonyms
        # return value
        return -1 # tweet won't get a reply   


    # generates a random integer from a..b inclusive
    def random(self, a, b):
        from random import randint
        # generates a random integer from a..b inclusive
        # this will be used to give different answers that means
        # the same to users
        return randint(a, b)


    # respond to mention based on a few details
    def respond_to_mention(self):
        if self.mentions:
            # single mention variable
            self.current_mention = self.mentions.pop(0)            
            # single key variable corresponding to mention that was just poped 
            self.current_filter = self.keys.pop(0)
            # single synonym variable corresponding to the synonym being used in the question
            self.current_synonym = self.synonyms.pop(0)
            
            # Debbugin
            # if self.debug: print "DEBUG - Geolocation is " + ("" if self.current_mention['user']['geo_enabled'] else "not ") + "enabled" 
            # print type(mention['user']['geo_enabled'])
            
            if ( self.current_mention['user']['geo_enabled'] == False ):
                # add time stamp to this message
                self.message = self.generate_message(result={'code': self.MSG_GEN_NO_LOCATION+self.random(-1, 0)}, username=self.current_mention['user']['screen_name']) 
            else:
                # make a querry to get incidents around the location?????                    
                # call to generate result - this makes a call to refresh 
                # wish returns issues around a longitude and latitude 
                result_ok = self.generate_result()
                # create message based on generate_result()
                self.message = self.generate_message(self.current_filter, result_ok, self.current_mention['user']['screen_name']) # "@" + self.current_mention['user']['screen_name'] + " " + self.you_mentioned_filter + item


    # get_latitude form the current mention
    def get_latitude(self):
        try:
            return str(self.current_mention['geo']['coordinates'][0])
        except TypeError:
            return -1
    
    
    # get_longitude from the current tweet
    def get_longitude(self):
        try:
            return str(self.current_mention['geo']['coordinates'][1])
        except TypeError:
            return -1
        

    # get list of errors
    def get_errors(self):
        import json
        
        error_codes = open('error.json')
        json_errors = json.load(error_codes)
        error_codes.close()
           
        return json_errors


    # get current file
    def get_current_time(self):
        from time import gmtime, strftime    
        return strftime("%a, %d %b %Y %X", gmtime()) + " "
        

    # replace ##FILTER## and ##LOCATION## 
    # tags from the error message
    def format_message(self, msg, issues_count=-1, filter="", location="", website=""):
        
        if (issues_count == 1):
            msg = msg.replace("<<ISARE>>", "is")           
        else:
            msg = msg.replace("<<ISARE>>", "are")
                
        msg = msg.replace("<<FILTER>>", filter)
        msg = msg.replace("<<LOCATION>>", location)
        msg = msg.replace("<<URL>>", website)
        msg = msg.replace("<<ISSUES>>", str(issues_count))
        
        return msg


    def tiny_url(self, url):
        import httplib
        data = ""
    
        conn = httplib.HTTPConnection("tinyurl.com")
        conn.request("GET", "/api-create.php?url="+url)
        result = conn.getresponse()
        print result.reason
        if (result.reason == "OK"):
            data = result.read()
            if self.debug: print "DEBUG - " + data
        
        conn.close()
        return data


    # generates a message based on the code
    ######
    def generate_message(self, item="", result={"code": -52}, username="EMPTY"):
        username = "@" + username
        
        code = result['code']
        
        current_time = self.get_current_time()   
        str_code = str(code)
        
        verb = "are" if (code > 1) else "is" # is vs are for more than 2 items
        if (code == 0): str_code = "no" # this will switch 0 for "no" in the message    
        
        
        if (code >= 0):            
            return current_time + username + " There " +verb+ " " +str_code+ " mentions of " +self.current_synonym+ " close to your location."
        if (code == self.MSG_CON_PIN_LOCATION_NEEDED):
            # forming the real url to request access to the map
            # self.tiny_url(self.website_url + "/mini.html?lat=" + str(result['closest_location'][0]) + "&lng=" + str(result['closest_location'][1]) + "&filter=" + item)
            # "-50": "The closest issue reported about <<FILTER>> is <<LOCATION>>"
            location_url = self.tiny_url( "http://" + self.website_url + "?lat=" + str(result['closest_location'][0]) + "&lng=" + str(result['closest_location'][1]) + "&filter=" + item )
            return current_time + " " + username + " " + self.format_message(self.error[str_code], filter=self.current_synonym, location=location_url)
        elif (code == self.MSG_REFER):
            # "-52": "Please refer to <<URL>> for more information."
            return current_time + " " + username + " " + self.format_message(self.error[str_code], website=self.website_address)
        elif (code == self.MSG_NUM_ISS_N_LOC or code == self.MSG_NUM_ISS_N_LOC_1):
            # "-54": "There <<ISARE>> <<ISSUES>> about <<FILTER>> reported. The closest being <<LOCATION>>"
            # "-55": "<<ISSUES>> mentions about <<FILTER>> have been found close to you. Find out more at <<LOCATION>>"
            location_url = self.tiny_url( "http://" + self.website_url + "?lat=" + str(result['closest_location'][0]) + "&lng=" + str(result['closest_location'][1]) + "&filter=" + item )
            return current_time + " " + username + " " + self.format_message(self.error[str_code], issues_count=result['count'], filter=self.current_synonym, location=location_url)
        
        return self.format_message(current_time + " " + username + " " + self.error[str_code], website=self.website_address)




    ##########
    def get_closest_distance(self, latitude, longitude, tweets):
        
        def calculate_distance(la1, lo1, la2, lo2):
            from math import sqrt, pow
            return sqrt(pow(la1-la2, 2)+pow(lo1-lo2, 2))
        
        location = []
        previous_distance = self.MAX_DISTANCE
        
        for tweet in tweets:
            lat = tweet['geo']['coordinates'][0]
            lon = tweet['geo']['coordinates'][1]
            distance = calculate_distance(float(latitude), float(longitude), float(lat), float(lon))
            if (distance < previous_distance):
                previous_distance = distance
                location = []
                location.insert(0, lat)
                location.insert(1, lon)
    
        return location



    # get results from backend
    # right now i'll be getting results from the same place bryan is getting his results for the UI
    #
    # reutrn value
    # json
    # "closest_location" : array of location [latitude, longitude]
    # 
    # "code" : if the number is negative then this corresponds to a code
    #
    ########
    def generate_result(self):
        import json
        import httplib
        
        latitude = self.get_latitude()
        longitude = self.get_longitude()
        filter = self.current_filter
        result = {}
        
        # if latitude and longitude is not available 
        # could be because the user is connected to wifi
        # we return -1 which is the error code for 
        # latitude and longitude not available
        ###
        if (latitude == -1 and longitude == -1):
            result['code'] = self.MSG_LAT_LON_NOT_AVAILABLE+random(-1,0)
            return result
            
        # we will analyze the question to see whether
        # it requires a fixed point to be returned
        pin_needed = self.analyze_question(self.current_mention['text'])
        if self.debug: print "DEBUG - This question requires a close point response " + str(pin_needed)
        # HTTP request initialization
        ####
        conn = httplib.HTTPConnection(self.website_url)
        conn.request("GET", "/cache?lat="+latitude+"&lng="+longitude+"&rad="+self.radius+"&olat=0&olng=0&orad=0&filter="+filter)
        http_result = conn.getresponse()
        # DEBUG message
        if self.debug: print "DEBUG - RESPONSE FROM getresponse() - " + http_result.reason
        # if response is OK then http was good
        # we can proceed
        ####
        if (http_result.reason == "OK"):
            data = http_result.read()
            # if self.debug: print "DEBUG - " + data
            json_data = json.loads(data)
            # if self.debug: print "DEBUG - " + str(type(json_data['t']))
            
            # if the data returned by the refresh 
            # is not empty then we go on
            # double check this condition sicne it may not be working
            # test case
            # when location is required in the answer and there are
            # no issues close to the location being requested
            ###
            if (json_data['t']):
                # here if pin_needed is true
                # we need to call a function that returns the
                # geo_location of the closes incident
                if (pin_needed != 0):
                    closest_location = self.get_closest_distance(latitude, longitude, json_data['t'])
                    if self.debug: print "DEBUG - " + str(closest_location)
                    result['closest_location'] = closest_location
                    # error code
                    # result['code'] = pin_needed
                    # gets the number of mentions of the issues
                    # result['count'] = len(json_data['f'])
                # if self.debug: print "DEBUG - " + json_data['t'][0]['text']
            else:
                # if json_data is empty 0 is returned
                result['count'] = 0
                result['code'] = 0
                conn.close()
                return result
            
        conn.close()
        
        result['count'] = len(json_data['f'])
        result['code']  = pin_needed
        
        # change this to return result
        return result



    # get the generated message so it can be posted back into twitter
    def get_message(self):
        return self.message



if __name__ == "__main__":
    bot = ChatBot()
    
    import json
    
    twitter_data = open('data.json')
    oauth = json.load(twitter_data)
    twitter_data.close()
        
    bot.respond_to_mention()