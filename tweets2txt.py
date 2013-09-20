#!/usr/bin/env python
from twython import Twython
from twython import TwythonStreamer
import oauth
import ConfigParser
import sys
import json
from datetime import datetime


config = ConfigParser.ConfigParser()
config.read('oauth')

APP_KEY = config.get('twitter', 'APP_KEY')
APP_SECRET = config.get('twitter', 'APP_SECRET')
OAUTH_TOKEN = config.get('twitter', 'OAUTH_TOKEN')
OAUTH_TOKEN_SECRET = config.get('twitter', 'OAUTH_TOKEN_SECRET')
LOG_FREQ=10

lasttime=datetime.now()

class MyStreamer(TwythonStreamer):
	def on_success(self, data):
		try:
			if 'text' in data:

				global lasttime
				print data['user']['screen_name'].encode('utf-8') + ": " +data['text'].encode('utf-8')

				nowtime=datetime.now()
				
				if(nowtime.minute-lasttime.minute<LOG_FREQ):
					stamp=lasttime.strftime("%Y%m%d_%H%M").lstrip("0")
				else:
					lasttime=datetime.now()
					stamp=nowtime.strftime("%Y%m%d_%H%M").lstrip("0")


				with open('tweets/'+stamp+'.txt', 'a') as outfile:
					json.dump(data, outfile)
					outfile.write('\n')
					
					
		except:
			print "Unexpected error:", sys.exc_info()[0]				
			
	def on_error(self, status_code, data):
		print status_code
		self.disconnect()
		db.close()

        # Want to stop trying to get data because of the error?
        # Uncomment the next line!
        # self.disconnect()

stream = MyStreamer(APP_KEY, APP_SECRET, OAUTH_TOKEN, OAUTH_TOKEN_SECRET)

#stream.statuses.filter(track = 'storm')
stream.statuses.filter(locations = '-82.177734,24.491523,-79.736023,27.009891')
#~ stream.statuses.filter(follow = 'justinbieber')

###### UNCOMMENT TO USE THE REST/SEARCH API
#~ 
#~ twitter = Twython(APP_KEY, APP_SECRET,OAUTH_TOKEN, OAUTH_TOKEN_SECRET)
#~ 
#~ try:
    #~ user_timeline = twitter.get_user_timeline(screen_name='AwesomeAndy')
#~ except TwythonError as e:
    #~ print e
#~ 
#~ for tweet in user_timeline :
	#~ 
	#~ print tweet['user']['screen_name'] + ": " + tweet['text']
	#~ if tweet['place']:
		#~ print tweet['place']['name']
