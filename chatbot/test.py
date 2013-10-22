def main():
    import httplib
    import json
       

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
        print key.upper()
    

if __name__ == "__main__":
    main()

"""
http://empanada.cs.fiu.edu/json/filters.json
"""