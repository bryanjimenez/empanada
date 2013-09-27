#!/usr/bin/python
from twython import Twython
import time

def EMPa():
	APP_KEY = 'i8yi6oyp4UtwoSGVBrqiVQ'
	APP_SECRET = 'SXGkWmdvjRYvWRCHUDhJSTkkI5uVhXkm8xHjgCdSw'
	ACCESS_TOKEN = "392397306-fgFNbrKfccVImUMLe64d6cgdy4gxffR8Tqc1pTCT"
	ACCESS_TOKEN_SECRET = "D2xjntYtbhL3jlPJwrKOhaYkxI99r5B1XWzNFMFc"
	
	
	twitter = Twython(APP_KEY, APP_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET)		# creates an instance of twython that allows access to
	
	"""
	twitter = Twython(APP_KEY, APP_SECRET, oauth_version=2)
	ACCESS_TOKEN = twitter.obtain_access_token()
	twitter = Twython(APP_KEY, access_token=ACCESS_TOKEN)
	"""
	
	"""search = twitter.search_gen("python")"""

	search = twitter.get_home_timeline()
			
	for result in search:
		print result['text']
			
	time.sleep(4)


def test():
	print "1"
	time.sleep(10)
	print "2"
		

if __name__ == "__main__":
    EMPa()
