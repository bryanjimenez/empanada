def json_read():
    import httplib
    import json
       

    json_filter = {}
    h1 = httplib.HTTPConnection("empanada.cs.fiu.edu")
    h1.request("GET", "/json/filters.json")
    result = h1.getresponse()
    print type(result.reason), result.reason
    if (result.reason == "OK"):
        filter = result.read()
        # print filter
        json_filter = json.loads(filter)
    h1.close()
    
    for key in json_filter:
        print key


def results_read():
    import json
    import httplib
    
    conn = httplib.HTTPConnection("empanada.cs.fiu.edu")
    conn.request("GET", "/refresh.php?lat=25.764084501106787&lng=-80.37422332275389&rad=4&olat=0&olng=0&orad=0&filter=fuel,zombies")
    result = conn.getresponse()
    print result.reason
    if (result.reason == "OK"):
        data = result.read()
        # print data
        json_data = json.loads(data)
        print json_data['t'][0]['text']
        print json_data['t'][0]['geo']['coordinates'][0]
        print json_data['t'][0]['geo']['coordinates'][1]
        
    conn.close()


def generate_result():
    import json
    import httplib
    
    radius = 4
    latitude = 25.764084501106787
    longitude = -80.37422332275389
    filter = current_filter = "zombies"
    
    conn = httplib.HTTPConnection("empanada.cs.fiu.edu")
    conn.request("GET", "/refresh.php?lat="+str(latitude)+"&lng="+str(longitude)+"&rad="+str(radius)+"&olat=0&olng=0&orad=0&filter=fuel,"+filter)
    result = conn.getresponse()
    print result.reason
    if (result.reason == "OK"):
        data = result.read()
        # print "DEBUG - " data
        json_data = json.loads(data)
        print json_data
        for item in json_data:
            print item.upper()
        print "DEBUG - " + str(len(json_data['t']))
        
    conn.close()


def time():
    from time import gmtime, strftime    
    print strftime("%a, %d %b %Y %X", gmtime())
    
    #from datetime import datetime
        
    #print str(datetime.now())
    
    # d = date    
    # print d.today()
    # print d


def str_format():
    
    a = "this is an <<ex>>"
    
    print a.replace("<<ex>>", "test")
        
    
def tiny_url():
    import httplib
    """
    
    conn = httplib.HTTPConnection("tinyurl.com")
    conn.request("GET", "/api-create.php?url=empanada.cs.fiu.edu")
    result = conn.getresponse()
    print result.reason
    if (result.reason == "OK"):
        data = result.read()
        print type(data)
    
    conn.close()
    """
    
def random_test():
    from random import randint
    
    print randint(-1, 0)    
    
def main():
    results_read()
    # json_read()
    # generate_result()
    # time()
    # str_format()
    # tiny_url()
    # random_test()
    
    
if __name__ == "__main__":
    main()

"""
http://empanada.cs.fiu.edu/json/filters.json
"""