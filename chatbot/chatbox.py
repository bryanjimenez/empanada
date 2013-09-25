#!/usr/bin/python
from twython import Twython

def EMPa():
	APP_KEY = 'i8yi6oyp4UtwoSGVBrqiVQ'
	APP_SECRET = 'SXGkWmdvjRYvWRCHUDhJSTkkI5uVhXkm8xHjgCdSw'
	
	
	twitter = Twython(APP_KEY, APP_SECRET, oauth_version=2)
	ACCESS_TOKEN = twitter.obtain_access_token()

	twitter = Twython(APP_KEY, access_token=ACCESS_TOKEN)
	
	search = twitter.search_gen("python")
	
	for result in search:
		print result['text']
		

if __name__ == "__main__":
    EMPa()
