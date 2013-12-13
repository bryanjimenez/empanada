#!/usr/bin/env python
from twython import Twython
from twython import TwythonStreamer
import oauth
import ConfigParser
import sys
import json
from datetime import datetime
import os


config = ConfigParser.ConfigParser()
config.read('oauth')

APP_KEY = config.get('twitter', 'APP_KEY')
APP_SECRET = config.get('twitter', 'APP_SECRET')
OAUTH_TOKEN = config.get('twitter', 'OAUTH_TOKEN')
OAUTH_TOKEN_SECRET = config.get('twitter', 'OAUTH_TOKEN_SECRET')
LINE_MAX=1
DATE_FORMAT='%Y%m%d_%H%M%S'
PATH2LOG='tweets/'


tstamp= datetime.now().strftime(DATE_FORMAT)
linecount=0


class MyStreamer(TwythonStreamer):
	def on_success(self, data):
		try:
			if 'text' in data:

				global tstamp
				global linecount
				
				print data['user']['screen_name'].encode('utf-8') + ": " +data['text'].encode('utf-8')
				
				tstamp= datetime.now().strftime(DATE_FORMAT)
					
				with open(PATH2LOG+tstamp+'.txt', 'a') as outfile:
					json.dump(data, outfile)
					outfile.write('\n')
					
				#~ os.system("/usr/local/hadoop/bin/hadoop fs -put tweets/*.txt /user/jonathan/pending/")
				#~ os.system("rm tweets/*.txt")
					
					
		except:
			print "Unexpected error:", sys.exc_info()[0]				
			
	def on_error(self, status_code, data):
		print status_code
		self.disconnect()
		db.close()


stream = MyStreamer(APP_KEY, APP_SECRET, OAUTH_TOKEN, OAUTH_TOKEN_SECRET)

stream.statuses.filter(locations = '-126.2109375,24.491523,-50.625,49.95121991')
#~ stream.statuses.filter(locations = '-86.443738,24.726875,-79.736023,27.009891')

#~ Sample Coordinates:
#~ Key West:
#~ 24.491523 -82.177734
#~ Jupiter:
#~ 27.009891 -79.736023
