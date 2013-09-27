#!/usr/bin/python
from twython import Twython, TwythonError
import time

def EMPa():
	import json
	
	twitter_data = open('oauth.json')
	oauth = json.load(twitter_data)
	twitter_data.close()
	
	twitter = Twython(oauth['APP_KEY'], oauth['APP_SECRET'], oauth['ACCESS_TOKEN'], oauth['ACCESS_TOKEN_SECRET'])
	twitter.verify_credentials()
			
	try:
		pass # twitter.update_status(status='See how easy this was?')
	except TwythonError as e:
		print e
		
	""" # authentication 2 method
	twitter = Twython(APP_KEY, APP_SECRET, oauth_version=2)
	ACCESS_TOKEN = twitter.obtain_access_token()
	twitter = Twython(APP_KEY, access_token=ACCESS_TOKEN)
	"""
	
	# search = twitter.search_gen("python")
	# search = twitter.get_home_timeline()
	"""		
	for result in search:
		print result['text']
	"""		
	
	

def test():
	time.sleep(10)

		

if __name__ == "__main__":
    EMPa()
