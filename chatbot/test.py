def main():
    filter = ["a","b","c"]
    
    for item in filter:
        if (item in "b;lkjl;kjl;jk;lj"):
            print "yes"
            break
        else:
            print "no"

if __name__ == "__main__":
    main()