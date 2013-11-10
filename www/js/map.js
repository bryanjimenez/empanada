//GOOGLE Globals
var geocoder;
var map;
var manager;

//EMPANADA Globals
var lat = 25.75906, lng = -80.37388, zoom = 14, rad = 1;
var olat = 0, olng = 0, ozoom = 14, orad = 0;
var mypos;
var DEBUG=true;

//SINGLETON
var Markers = {
    markers: [],
    markerst: [],
    window: null,
    toggle: function(category, t) {
        if (this.window)
            this.window.close();

        for (var i = 0; i < this.markers.length; i++)
            if (this.markerst[i] == category)
                this.markers[i].setVisible(t);
    },
    push: function(m, f, c) {
        this.markers.push(m);
        this.markerst.push(f);


        google.maps.event.addListener(m, 'click', function() {

            //close infobubble if we click on other bubble
            if (Markers.window)
                Markers.window.close();

            Markers.window = new google.maps.InfoWindow({
                content: c,
                arrowStyle: 2,
                arrowSize: 10,
                ShadowStyle: 1

            });
            Markers.window.open(map, m);
        });
    }
}

//SINGLETON
var legend;
function Legend(parent) {
    var _this = this;
    var forbidden = "image/forbidden.png";

    var filters = [];
    var prefilters;
    var icons;
    this.div = parent;

    this.add = function(item) {
        filters.push(item);
    };

    this.remove = function(item) {
        filters.splice(filters.indexOf(item), 1);
    };

    this.getFilters = function() {
        return filters;
    };
    this.getIcons = function(key) {
        return icons[key].icon;
    };
    this.setPreFilters = function(f) {
        prefilters = f.split(",");
        //alert(prefilters);
    }
    this.toggle = function(obj) {
        var name = obj.id.toLowerCase().split('_')[0];
        if (obj.innerHTML == '') {
            obj.innerHTML = '<img src="' + forbidden + '">';
            this.remove(name);
            //toggleMarkers(name,false);
            Markers.toggle(name, false);
        }
        else {
            obj.innerHTML = '';
            this.add(name);
            Markers.toggle(name, true);
        }
    };

    document.getElementById('legendtitle').onclick = function() {

        if (_this.div.style.height == '20px')
            _this.div.style.height = '';
        else
            _this.div.style.height = '20px';
    }
    
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function(par) {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
        {
            //alert(xmlHttp.responseText);
            icons = JSON.parse(xmlHttp.responseText);
            for (var key in icons) {
                key = key.toLowerCase();
                //(key);

                filters.push(key);
                //alert(this.filters[0]);
                var type = icons[key];
                var name = type.name;
                var icon = "image/red/" + type.icon;
                var div = document.createElement('div');

                if (prefilters && prefilters.indexOf(key) < 0) {
                    div.innerHTML = '<div id="' + key + '_filter" title="' + name + '" style="width:32px;height:37px;background-image: url(' + icon + ')" onclick="legend.toggle(this);"><img src="' + forbidden + '"></div> ';
                    _this.remove(key);
                } else {
                    div.innerHTML = '<div id="' + key + '_filter" title="' + name + '" style="width:32px;height:37px;background-image: url(' + icon + ')" onclick="legend.toggle(this);"></div> ';
                }
                parent.appendChild(div);
            }
            for (var i in prefilters) {
                //alert(i);
            }

            //Do a refresh once filters are up
            //alert(legend.getFilters());
            refresh();
            //fpl();
        }
    }
    xmlHttp.open("GET", "json/filters.json", true);
    xmlHttp.send(null);
}

function User() {
    var _this = this;

}


//SINGLETON
var places;
function Places() {

    //https://developers.google.com/places/training/additional-places-features
    this.searchNear = function(obj) {
        var request = {
            location: new google.maps.LatLng(lat, lng),
            radius: 500,
            //types: ['restaurant']
            keyword: obj.value
        };
        //infowindow = new google.maps.InfoWindow();
        var service = new google.maps.places.PlacesService(map);
        service.nearbySearch(request, callback);
    };

    function callback(results, status) {
        if (status == google.maps.places.PlacesServiceStatus.OK) {
            for (var i = 0; i < results.length; i++) {
                var request = {
                    reference: results[i].reference
                };
                service = new google.maps.places.PlacesService(map);
                service.getDetails(request, callback_detail);
            }
        }
    }
    function callback_detail(place, status) {
        if (status == google.maps.places.PlacesServiceStatus.OK)
            createMarker(place);
    }

    //https://developers.google.com/places/documentation/search#PlaceSearchRequests 
    //https://developers.google.com/maps/documentation/javascript/examples/place-search-pagination  
    function createMarker(place) {
        var contentString = '' +
                '<div id="content" style="width:304px;height:176px;">' +
                '<div id="siteNotice">' +
                '<span style="font-weight:400">' + place.name + '</span>' +
                '</div>' +
                '<div id="bodyContent" style="float:left;border:0px solid blue;height:100px;">' +
                '<span>' + place.formatted_address + '<br/><br/>' +
                '' + (place.website == null ? '' : '<a href="' + place.website + '" target="_blank">' + place.website + '</a>') + '<br/>' +
                '' + (place.formatted_phone_number == null ? '' : place.formatted_phone_number) + '<br/>' +
                '' + (place.rating == null ? '' : 'Rating: ' + place.rating) + '</span><br/><br/>' +
                '<a href="' + (place.url == null ? '' : place.url) + '" target="_blank">More Info</a>' +
                '</div>' +
                '</div>';

        var image = {
            url: place.icon,
            size: new google.maps.Size(71, 71),
            origin: new google.maps.Point(0, 0),
            anchor: new google.maps.Point(17, 34),
            scaledSize: new google.maps.Size(25, 25)
        };

        var marker = new google.maps.Marker({
            map: map,
            icon: image,
            position: place.geometry.location
        });

        google.maps.event.addListener(marker, 'click', function() {
            Markers.window.setContent(contentString);
            Markers.window.open(map, this);
        });
    }
}


//SINGLETON
var compass;
function Compass(obj) {
    var _this = this;
    var mymarker;
    var watching = null;
    var needle_off = 'image/arrow_off.png';
    var needle_on = 'image/arrow_on.png'
    this.obj = obj;

    this.htmlObj = obj;



    this.obj.onclick = function() {
        _this.toggle();
    };



    /*
     * Great place to test google api code
     * http://code.google.com/apis/ajax/playground/
     */
    this.dropPin = function(t) {
        //toggle off
        if (!t && mymarker)
            mymarker.setMap(null);
        else {
            map.setCenter(mypos);

            if (mymarker)
                mymarker.setMap(null);

            mymarker = new google.maps.Marker({
                position: mypos,
                map: map,
                //animation: google.maps.Animation.BOUNCE,
                icon: 'image/red.png',
                title: 'You\'re Here'
            });
        }
    }

    /*
     * http://www.w3schools.com/html/html5_geolocation.asp
     */
    var options = {
        enableHighAccuracy: true,
        timeout: 5000,
        maximumAge: 0
    };

    function success(pos) {
        lat = pos.coords.latitude;
        lng = pos.coords.longitude;
        mypos = new google.maps.LatLng(lat, lng);
        _this.dropPin(true);
    }

    function error(err) {
        var msg;

        switch (error.code)
        {
            case error.PERMISSION_DENIED:
                msg = "User denied the request for Geolocation."
                break;
            case error.POSITION_UNAVAILABLE:
                msg = "Location information is unavailable."
                break;
            case error.TIMEOUT:
                msg = "The request to get user location timed out."
                break;
            case error.UNKNOWN_ERROR:
                msg = "An unknown error occurred."
                break;
        }
        _this.off();

        //console.warn('ERROR(' + err.code + '): ' + err.message);
        alert("Geolocation is not supported by your browser at this time.");
        //alert(msg);
    }
    ;

    this.off = function() {
        _this.htmlObj.style.backgroundImage = 'url(\'' + needle_off + '\')';
        //disableMovement(false);
        navigator.geolocation.clearWatch(watching);
        watching = null;
        _this.dropPin(false);
    };

    this.toggle = function() {
        // tracking mode OFF
        if (watching) {
            _this.htmlObj.style.backgroundImage = 'url(\'' + needle_off + '\')';
            //disableMovement(false);
            navigator.geolocation.clearWatch(watching);
            watching = null;
            _this.dropPin(false);
        }
        // tracking mode ON
        else {
            //disableMovement(true);
            _this.htmlObj.style.backgroundImage = 'url(\'' + needle_on + '\')';
            watching = navigator.geolocation.watchPosition(success, error, options);
            //refresh(); 	
        }
    };
}


//STATIC Class(Object) MAP
var Map = {
    //15 = 1/2, 14 = 1, 13 = 2, 12 = 4, 11 = 8, 10 = 16
    //-1 = 1/2, 0 = 1, 1 = 2, 2 = 4, 3 = 8, 4 = 16
    //2^(14-zoom) = radius
    //ln(x^y) = y*ln(x)
    //ln(2^(14-zoom)) = ln(radius) = (14-zoom) * ln(2)
    //ln(radius)/ln(2) -14 = -zoom
    //zoom = 14 - ln(radius)/ln(2)
    //throw  round on there to make sure we get an integer
    //and don't make the g-maps API angry

    rad2zoom: function(radius) {
        return Math.round(14 - Math.log(radius) / Math.LN2);
    },
    zoom2rad: function(zoom) {
        return Math.round(Math.pow(Math.E, (14 - zoom) / Math.LN2));
    }
}





//https://developers.google.com/maps/documentation/javascript/reference#Map




function initialize() {

    zipshow = document.getElementById('zipshow');
    legend = new Legend(document.getElementById('legend'));


    //TODO 
    //do better error check here FIX THIS
    if (location.search.indexOf('lat=') > -1)
        lat = location.search.split('lat=')[1].split('&')[0];
    if (location.search.indexOf('lng=') > -1)
        lng = location.search.split('lng=')[1].split('&')[0];
    if (location.search.indexOf('filter=') > -1)
        legend.setPreFilters(location.search.split('filter=')[1].split('&')[0]);
   if (location.search.indexOf('zoom=') > -1)
        zoom = parseInt(location.search.split('zoom=')[1].split('&')[0]);					//this needs to be a number


    compass = new Compass(document.getElementById('findme'));
    places = new Places();

    geocoder = new google.maps.Geocoder();

    var mapOptions = {
        zoom: zoom,
        center: new google.maps.LatLng(lat, lng),
        mapTypeId: google.maps.MapTypeId.ROADMAP,
        //mapTypeId: google.maps.MapTypeId.SATELLITE,
        disableDefaultUI: true
    };
    map = new google.maps.Map(document.getElementById('map-canvas'),
            mapOptions);

    var mgrOptions = {borderPadding: 50, maxZoom: 1};
    manager = new MarkerManager(map, mgrOptions);


    // EVENT HANDLERS

    zipshow.onchange = function() {
        document.getElementById('zip').style.display = zipshow.checked ? "inline" : "none";
    }

    google.maps.event.addListener(map, 'click', function() {
        //close infobubble if we click on  map
        if (Markers.window)
            Markers.window.close();
    });
    google.maps.event.addListener(map, 'dragend', function() {
        refresh();
//        fpl();

        compass.off();
    });

    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(legend.div);
    map.controls[google.maps.ControlPosition.LEFT_BOTTOM].push(compass.htmlObj);
}




/* geocoding
 * 
 * https://developers.google.com/maps/documentation/javascript/geocoding?csw=1
 */

// REVERSE GEOCODING
//https://developers.google.com/maps/documentation/geocoding/#ReverseGeocoding
//maps.google.com/maps/api/geocode/json?latlng=25.705501,-80.359855&components=postal_code&sensor=false

function codeAddress() {
    var address = document.getElementById("zip").value;
    geocoder.geocode({'address': address}, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {

            mypos = results[0].geometry.location;
            map.setCenter(mypos);
            refresh();

        } else {
            alert("Geocode was not successful for the following reason: " + status);
        }
    });
}

/* Thanks to
 * http://www.fplmaps.com/
 */
function fpl() {

    var xmlHttp = null;

    xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function()
    {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200 && xmlHttp.responseText)
        {

            //alert(xmlHttp.responseText);

            var fpl = JSON.parse(xmlHttp.responseText);
            //alert(tweets.f[0]);

            for (var i in fpl.o) {
                var filter = "poweroutage";
                var outage = fpl.o[i];

                if (!outage.status)
                    continue;

                var cause = outage.Cause;
                var pic = "http://www.fpl.com/fplcommon/wrapper/1024images/w_topLogo.gif";
                var status = outage.status;
                var reported = "Reported " + outage.dateReported;
                var updated = "Updated " + outage.lastUpdated;


                var x = outage.lat;
                var y = outage.lng;
                var contentString = '' +
                        '<div id="content" style="width:304px; height:176px;">' +
                        '<div id="siteNotice">' +
                        '<div style="float:left;border:0px solid blue;">' +
                        '<h4 id="firstHeading" class="firstHeading">' + cause + '</h4>' +
                        '</div>' +
                        '<div style="float:right;border:0px solid red;width=50%;">' +
                        '</div>' +
                        '</div>' +
                        '<div id="left" style="float:center;border:0px solid green;width:100%;">' +
                        '</div>' +
                        '<div id="bodyContent" style="float:left;border:0px solid blue;height:100px;">' +
                        '<div style="float:left;border:0px solid blue;width:204px;">' +
                        '<p style="max-width:200px">' + status + '</p>' +
                        '</div>' +
                        '<div style="float:right;border:0px solid red;width=100%;height=100%;">' +
                        '<img name="userpic" src="' + pic + '"></img>' +
                        '</div>' +
                        '</div>' +
                        '<div id="footer" style="float:right;">' +
                        '<p>' + reported + '<br/>' + updated + '</p>' +
                        '</div>' +
                        '</div>';

                var marker = new google.maps.Marker({
                    position: new google.maps.LatLng(x, y),
                    //map: map,
                    icon: "image/orange/" + legend.getIcons(filter),
                    //animation: google.maps.Animation.DROP,
                    title: status
                });


                Markers.push(marker, filter, contentString);


                manager.addMarker(marker, 12);
                manager.refresh();
            }
        }
    }


    var s = "fpl.php?lat=" + lat + "&lng=" + lng + "&rad=" + rad + "&olat=" + olat + "&olng=" + olng + "&orad=" + orad;
    //alert(s);
    olat = lat;
    olng = lng;
    orad = Map.zoom2rad(zoom);

    xmlHttp.open("GET", s, true);

    //xmlHttp.open("GET", "StormFeedRestoreZoom2.json", true);
    xmlHttp.send(null);
}

function refresh() {

    var xmlHttp = new XMLHttpRequest();

    xmlHttp.onreadystatechange = function()
    {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200 && xmlHttp.responseText)
        {
            //if(!xmlHttp.responseText)
            //	return;

            var tweets = JSON.parse(xmlHttp.responseText);

			DEBUG&&console.log(tweets.t.length);

            //alert(tweets.t.length);

            for (var i in tweets.t) {
                //alert(i);
                var filter = tweets.f[i];
                var tweet = tweets.t[i];

                var user = tweet.user.screen_name;
                var pic = tweet.user.profile_image_url;
                var text = tweet.text;
                var follow = tweet.user.followers_count;
                var time = tweet.created_at;
                //var d = new Date(time.split[);
                //var date = d.getMonth() + 1 + "/" + d.getDay() + "/" + d.getFullYear();
                var date = time.split(" ")[1]+" "+time.split(" ")[2]+" "+time.split(" ")[5];

                //"coordinates": {"type": "Point", "coordinates": [-81.68738214, 27.96855823]}
                if (tweet.geo) {
                    var x = tweet.geo.coordinates[0];
                    var y = tweet.geo.coordinates[1];
                }
                else {
                    //TODO we need to come up with something better here
                    var x = -81.68738214;
                    var y = 27.96855823;
                }

                var contentString = '' +
                        '<div id="content" style="width:304px; height:176px;">' +
                        '<div id="siteNotice">' +
                        '<div style="float:left;border:0px solid blue;">' +
                        '<a href="https://twitter.com/' + user + '" target="_blank"><h4 id="firstHeading" class="firstHeading">' + user + '</h4></a>' +
                        '</div>' +
                        '<div style="float:right;border:0px solid red;width=50%;">' +
                        '<p>+/-' + follow + '</p>' +
                        '</div>' +
                        '</div>' +
                        '<div id="bodyContent" style="float:left;border:0px solid blue;height:100px;">' +
                        '<div style="float:left;border:0px solid blue;width:204px;">' +
                        '<p style="max-width:200px">' + text + '</p>' +
                        '</div>' +
                        '<div style="float:right;border:0px solid red;width=100%;height=100%;">' +
                        '<img name="userpic" src="' + pic + '"></img>' +
                        '</div>' +
                        '</div>' +
                        '<div id="options" style="float:left;border:0px solid blue;width:100%;">' +
                        '<div id="footer" style="float:right;border:0px solid black;">' +
                        '<p>' + date + '</p>' +
                        '</div>' +
                        //http://stackoverflow.com/questions/12823579/
                        //Open iOS 6 native map from URL
                        '<a href="http://maps.google.com/maps/?saddr=Current%20Location&daddr=' + user + '@' + x + ',' + y + '">Go here</a> | ' +
                        '<a href="javascript:showSearch();">Search Nearby</a>' +
                        '<div id="detail" style="visibility:hidden;float:left;border:0px solid blue;width:100%;">' +
                        '<input id="target" type="text"><button id="search" onclick="places.searchNear(this.previousSibling)" >Search</button>' +
                       '</div>' +
                        '</div>' +
                        '</div>';

                var marker = new google.maps.Marker({
                    position: new google.maps.LatLng(x, y),
                    //map: map,
                    icon: "image/red/" + legend.getIcons(filter),
                    animation: google.maps.Animation.DROP,
                    title: text
                });

                /*markers.push(marker);
                 markerst.push(filter);
                 */
                Markers.push(marker, filter, contentString);
                manager.addMarker(marker, 1);
                manager.refresh();

                //infobubble(marker, contentString);
            }
        }
    }
    //PARAMETERS
    lat = map.getCenter().lat();
    lng = map.getCenter().lng();
    zoom = map.getZoom();
    rad = Map.zoom2rad(zoom);



    var s = "refresh.php?lat=" + lat + "&lng=" + lng + "&rad=" + rad + "&olat=" + olat + "&olng=" + olng + "&orad=" + orad + "&filter=" + legend.getFilters();
	DEBUG&&console.log(s);


    xmlHttp.open("GET", s, true);
    xmlHttp.send(null);
}


function showSearch() {
    document.getElementById('detail').style.visibility = '';
}






//EVENTS

google.maps.event.addDomListener(window, 'load', initialize);


/*
 function disableMovement(disable) {
 var mapOptions;
 
 
 if (disable) {
 mapOptions = {
 //draggable: false,
 //scrollwheel: false,
 disableDoubleClickZoom: true,
 //zoomControl: false
 };
 } else {
 mapOptions = {
 //draggable: true,
 //scrollwheel: true,
 disableDoubleClickZoom: false,
 //zoomControl: true
 };
 }
 map.setOptions(mapOptions);
 }*/
