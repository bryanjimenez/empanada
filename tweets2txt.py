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
LINE_MAX=1000
DATE_FORMAT='%Y%m%d_%H%M%S'


tstamp= datetime.now().strftime(DATE_FORMAT)
linecount=0

class MyStreamer(TwythonStreamer):
	def on_success(self, data):
		try:
			if 'text' in data:

				global tstamp
				global linecount
				
				print data['user']['screen_name'].encode('utf-8') + ": " +data['text'].encode('utf-8')

				
				if(linecount<LINE_MAX):
					linecount+=1
				else:
					linecount=0
					tstamp= datetime.now().strftime(DATE_FORMAT)

				with open('tweets/'+tstamp+'.txt', 'a') as outfile:
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
