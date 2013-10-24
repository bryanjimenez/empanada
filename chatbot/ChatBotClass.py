#!/usr/bin/python
class ChatBot():
    def __init__(self):
        self.debug = True
        
        self.website_url = "empanada.cs.fiu.edu"
        self.mentions = []
        self.keys = []
        self.radius = "4"
        self.current_filter = "" # current filter
        self.current_mention = {} # current mention
        self.question = False
        self.filter = self.get_filters()
        self.keyword = '@empanada305'
        self.twitter_username = "empanada305" # hardcoded not a good idea
        
        # Messages (Strings)
        self.website_address = "our website"
        self.message  = "Please refer to " + self.website_address + " for more information."
        self.general_message_no_location = "Your location is not available. Please refer to " + self.website_address + " for more information."
        self.you_mentioned_filter = "You mentioned something about "

        

    # read filters from json in http://empanada.cs.fiu.edu/json/filters.json
    #
    def get_filters(self):
        import json
        import httplib
        
        json_filters = {}        
        conn = httplib.HTTPConnection(self.website_url)
        conn.request("GET", "/json/filters.json")
        response = conn.getresponse()
        if (response.reason == "OK"):
            data = response.read()
            json_filters = json.loads(data)
            
        conn.close()
        return json_filters
    


    # adds incoming tweets to metions if the contain the self.keyword phrase
    # it won't get added if the tweet came from the same username
    # right now i'm just checking that the tweet mentions @empanada305
    # we can remove this later to analyze all incoming tweets
    def add_to_mention(self, tweet):
        if ( tweet['user']['screen_name'] == self.twitter_username ): # if tweet is from the same username then we ignore the tweet
            if self.debug: print "DEBUG - " + tweet['user']['screen_name'] + " came from same user"
            return -1 # tweet won't get a reply
        # for key in self.keyword: # this could be remove or we could add more keywords
        if ( str.upper(self.keyword) in tweet['text'].upper() ): # if the tweet mentions @empanada305 then we move on to find the filter in the text
            for filter_key in self.filter:
                if ( filter_key.upper() in tweet['text'].upper() ):
                    if self.debug: print "DEBUG - added the tweet to the queue"
                    self.mentions.append(tweet)
                    self.keys.append(filter_key)
                    return 0; # tweet had the self.keyword in it

        return -1 # tweet won't get a reply   



    # respond to mention based on a few details
    def respond_to_mention(self):
        if self.mentions:
            self.current_mention = self.mentions.pop(0) # single mention variable
            self.current_filter = self.keys.pop(0) # single key variable corresponding to mention that was just poped
            if self.debug: print "DEBUG - Geolocation is " + ("" if self.current_mention['user']['geo_enabled'] else "not ") + "enabled" # Debbugin
            # print type(mention['user']['geo_enabled'])
            if ( self.current_mention['user']['geo_enabled'] == False ):
                self.message = "@" + self.current_mention['user']['screen_name'] + " " + self.general_message_no_location
            else:
                # make a querry to get incidents around the location?????                    
                # call to generate result - this makes a call to refresh wish returns issues around a longitude and latitude 
                result_ok = self.generate_result()
                # create message based on generate_result()
                if (result_ok): self.message = generate_message(item) # "@" + self.current_mention['user']['screen_name'] + " " + self.you_mentioned_filter + item


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
        

    #
    def generate_message(self, item):
        return "@" + self.current_mention['user']['screen_name'] + " " + self.you_mentioned_filter + item


    # get results from backend
    # right now i'll be getting results from the same place bryan is getting his results for the UI
    def generate_result(self):
        import json
        import httplib
        
        latitude = self.get_latitude()
        longitude = self.get_longitude()
        filter = self.current_filter
        
        if (latitude == -1 and longitude == -1):
            return False
            
        conn = httplib.HTTPConnection(self.website_url)
        conn.request("GET", "/refresh.php?lat="+latitude+"&lng="+longitude+"&rad="+radius+"&olat=0&olng=0&orad=0&filter=fuel,"+filter)
        result = conn.getresponse()
        if self.debug: print "DEBUG - RESPONSE FROM getresponse() - " + result.reason
        if (result.reason == "OK"):
            data = result.read()
            if self.debug: print "DEBUG - " + data
            json_data = json.loads(data)
            if self.debug: print "DEBUG - " + json_data['t'][0]['text']
            
        conn.close()
        return True



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