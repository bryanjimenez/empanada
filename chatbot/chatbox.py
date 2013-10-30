#!/usr/bin/python
from twython import Twython, TwythonError, TwythonStreamer
import time
from ChatBotClass import *

bot = ChatBot()

class MyStreamer(TwythonStreamer):
	
	def on_success(self, data):
    		if 'text' in data:
        		print data['text'].encode('utf-8')
        		post_back = bot.add_to_mention( data )
        		if ( post_back == 0 ):
        			print "DEBUG - RESPONDING TO MENTION"
        			bot.respond_to_mention()
        		 	print "DEBUG - " + bot.get_message()
        		  	# update_status_with(get_oauthentication(), bot.get_message())

	def on_error(self, status_code, data):
		print status_code

		# Want to stop trying to get data because of the error?
		# Uncomment the next line!
		# self.disconnect()

def get_oauthentication():
	import json
	
	twitter_data = open('oauth.json')
	oauth = json.load(twitter_data)
	twitter_data.close()
	
	return oauth


def update_status_with(oauth, message):
		
	twitter = Twython(oauth['APP_KEY'], oauth['APP_SECRET'], oauth['ACCESS_TOKEN'], oauth['ACCESS_TOKEN_SECRET'])
	twitter.verify_credentials()
			
	try:
		twitter.update_status(status=message)
	except TwythonError as e:
		print e


def streamer():
	oauth = get_oauthentication()
	stream = MyStreamer(oauth['APP_KEY'], oauth['APP_SECRET'], oauth['ACCESS_TOKEN'], oauth['ACCESS_TOKEN_SECRET'])
	# stream.statuses.filter(track='gas')
	stream.user()


def sleep(time):
	time.sleep(time)


def search_for(str):
	oauth = get_oauthentication()
	twitter = Twython(oauth['APP_KEY'], oauth['APP_SECRET'], oauth_version=2)
	ACCESS_TOKEN = twitter.obtain_access_token()
	twitter = Twython(oauth['APP_KEY'], access_token=ACCESS_TOKEN)
	
	search = twitter.search_gen(str)
	# search = twitter.get_home_timeline()
			
	for result in search:
		print result['text']
	
if __name__ == "__main__":
    streamer()
    # search_for("gas")
    # update_status_with(get_oauthentication(), "This message")
