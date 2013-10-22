#!/usr/bin/python
class ChatBot():
    def __init__(self):
        self.mentions = []
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
        conn = httplib.HTTPConnection("empanada.cs.fiu.edu")
        conn.request("GET", "/json/filters.json")
        response = conn.getresponse()
        if (response == "OK"):
            data = response.read()
            json_filters = json.loads(data)
            
        conn.close()
        return json_filters
    

    # adds incoming tweets to metions if the contain the self.keyword phrase
    # it won't get added if the tweet came from the same username
    def add_to_mention(self, tweet):
        if ( tweet['user']['screen_name'] == self.twitter_username ):
            print "DEBUG - " + tweet['user']['screen_name'] + " came from same user"
            return -1 # tweet won't get a reply
        for key in self.keyword:
            if ( key.upper() in tweet['text'].upper() ):
                print "DEBUG - added the tweet to the queue"
                self.mentions.append(tweet)
                return 0; # tweet had the self.keyword in it

        return -1 # tweet won't get a reply   


    # respond to mention based on a few details
    def respond_to_mention(self):
        if self.mentions:
            mention = self.mentions.pop(0) # single mention variable
            print "DEBUG - Geolocation is " + ("" if mention['user']['geo_enabled'] else "not ") + "enabled" # Debbugin
            # print type(mention['user']['geo_enabled'])
            if ( mention['user']['geo_enabled'] == False ):
                self.message = "@" + mention['user']['screen_name'] + " " + self.general_message_no_location
            else:
                for item in self.filter:
                    if ( item in mention['text'] ):
                        # Make a querry to get incidents around the location?????
                        self.message = "@" + mention['user']['screen_name'] + " " + self.you_mentioned_filter + item



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