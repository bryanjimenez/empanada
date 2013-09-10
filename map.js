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
	position: new google.maps.LatLng(25.75900,-80.37388),
	map: map,
	title: 'You\'re Here' + i
	});

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
}

var iconBase = 'https://maps.google.com/mapfiles/kml/shapes/';
var icons = {
	parking: {
		name: 'Parking',
		icon: iconBase + 'parking_lot_maps.png',
		shadow: iconBase + 'parking_lot_maps.shadow.png'
	},
	library: {
		name: 'Library',
		icon: iconBase + 'library_maps.png',
		shadow: iconBase + 'library_maps.shadow.png'
	},
	info: {
		name: 'Info',
		icon: iconBase + 'info-i_maps.png',
		shadow: iconBase + 'info-i_maps.shadow.png'
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



google.maps.event.addDomListener(window, 'load', initialize);

var legend = document.getElementById('legend');
for (var style in styles) {
var name = style.name;
var icon = style.icon;
var div = document.createElement('div');
div.innerHTML = '<img src="' + icon + '"> ' + name;
legend.appendChild(div);
}

