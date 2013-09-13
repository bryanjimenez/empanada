var geocoder;
var map;


function initialize() {
	geocoder = new google.maps.Geocoder();

	var mapOptions = {
	zoom: 18,
	center: new google.maps.LatLng(25.75906,-80.37388),
	mapTypeId: google.maps.MapTypeId.ROADMAP
};
map = new google.maps.Map(document.getElementById('map-canvas'),
mapOptions);

for (var i = 0; i < 2; i++) {
	// init markers
	var marker = new google.maps.Marker({
	position: new google.maps.LatLng(25.75896,-80.37402),
	map: map,
	title: 'You\'re Here' + i
	});
	
	infobubble(marker, i)

/*
	// process multiple info windows
	(function(marker, i) {
	// add click event
	google.maps.event.addListener(marker, 'click', function() {
	infowindow = new google.maps.InfoWindow({
	content: 'You\'re Here'
	});
	infowindow.open(map, marker);
	});
	})(marker, i);
	* 
	* */
}
/* some icons are listed here
 * 
 * http://kml4earth.appspot.com/icons.html
 */
var iconBase = 'https://maps.google.com/mapfiles/kml/shapes/';
var icons = {
	storm: {
		name: 'Storm',
		icon: iconBase + 'thunderstorm_maps.png',
		shadow: iconBase + 'thunderstorm_maps.shadow.png'
	},
	fire: {
		name: 'Fire',
		icon: iconBase + 'firedept_maps.png',
		shadow: iconBase + 'firedept_maps.shadow.png'
	},
	damage: {
		name: 'Damage',
		icon: iconBase + 'caution_maps.png',
		shadow: iconBase + 'caution_maps.shadow.png'
	},
	flood: {
		name: 'Flood',
		icon: iconBase + 'water_maps.png',
		shadow: iconBase + 'water_maps.shadow.png'
	}
};

var legend = document.getElementById('legend');
for (var key in icons) {
	var type = icons[key];
	var name = type.name;
	var icon = type.icon;
	var div = document.createElement('div');
	div.innerHTML = '<img src="' + icon + '"> ' + name;
	legend.appendChild(div);
}

map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(legend);

}
/* geocoding
 * 
 * https://developers.google.com/maps/documentation/javascript/geocoding?csw=1
 */

function codeAddress() {
	var address = document.getElementById("zip").value;
	geocoder.geocode( { 'address': address}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			map.setCenter(results[0].geometry.location);
			var marker = new google.maps.Marker({
			map: map,
			position: results[0].geometry.location
			});
			//alert(results[0].geometry.location);
		} else {
			alert("Geocode was not successful for the following reason: " + status);
		}
	});
}

function refresh(){
	

    var xmlHttp = null;

    xmlHttp = new XMLHttpRequest();
    xmlHttp.open( "GET", "refresh.php", false );
    xmlHttp.send( null );

    if(xmlHttp.status == 200) {
			//alert(xmlHttp.responseText);
			
			var json = xmlHttp.responseText;
			var obj = JSON.parse(json);
			//alert(obj.markers.length);
			for (var i = 0; i < obj.markers.length; i++) {
			  //alert(obj.markers[i].text);
			  // Do something with element i.
			  
			var x=obj.markers[i].geo.split(',')[0];
			var y=obj.markers[i].geo.split(',')[1];
			//alert("'"+x+"|"+y+"'");
			
			var marker = new google.maps.Marker({
			position: new google.maps.LatLng(x,y),
			map: map,
			title: obj.markers[i].text
			});
			  
			infobubble(marker, i);			  
			 
			  
			}


    }
    
}

function infobubble(marker, i) {
	  var contentString = '<div id="content">'+
      '<div id="siteNotice">'+
      '</div>'+
      '<a href=""><h1 id="firstHeading" class="firstHeading">UserName</h1></a>'+
      '<img width="70px" src=https://1.gravatar.com/avatar/1ee26eb5d1e53a3b9c5e45693c68ae1f?d=https%3A%2F%2Fidenticons.github.com%2Fe1895e3d1b333d255d3c801a228f8165.png&s=420"></img><div id="bodyContent">'+
      '<p>This is where user <b>UserName</b>, will have his tweet appear. ' +
		'</p>'+
      '</div>'+
      '</div>';

	
	
	
	
	// add click event
	google.maps.event.addListener(marker, 'click', function() {
	infowindow = new google.maps.InfoWindow({
	content: contentString,
	arrowStyle: 2,
	arrowSize: 10,
	ShadowStyle: 1
	
	});
	infowindow.open(map, marker);
	});
}

google.maps.event.addDomListener(window, 'load', initialize);

var legend = document.getElementById('legend');
for (var style in styles) {
var name = style.name;
var icon = style.icon;
var div = document.createElement('div');
div.innerHTML = '<img src="' + icon + 'width="50%"' +'> ' + name;
legend.appendChild(div);
}

