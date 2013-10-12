<?php
//header('Expires: Sun, 20 Jan 1985 00:00:00 GMT'); // date in the past
header('Cache-Control: no-cache, must-revalidate');
header('Content-type: application/json');
//header('Pragma: no-cache');

// Report all errors except E_NOTICE
// This is the default value set in php.ini
error_reporting(E_ALL ^ E_NOTICE);


function getZipcode($x, $y){
	//GOOGLE API URL with lat & lng inserted
	$s="http://maps.google.com/maps/api/geocode/json?latlng=".$x.",".$y."&components=postal_code&sensor=false";

	//RESPONSE IS JSON with results
	$response = json_decode(file_get_contents($s));

	//PARSE JSON and return zip only
	$zip=$response->results[0]->address_components[5]->long_name;

	return $zip;
}

//http://stackoverflow.com/questions/1502590/calculate-distance-between-two-points-in-google-maps-v3
function distance($lat1, $lon1, $lat2, $lon2, $unit='N') 
{ 
	//if(is_null($lat2)) return 0;

	$theta = $lon1 - $lon2; 
	$dist = sin(deg2rad($lat1)) * sin(deg2rad($lat2)) +  cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * cos(deg2rad($theta)); 
	$dist = acos($dist); 
	$dist = rad2deg($dist); 
	$miles = $dist * 60 * 1.1515;
	$unit = strtoupper($unit);

	if ($unit == "K") {
		return ($miles * 1.609344); 
	} else if ($unit == "N") {
		return ($miles * 0.8684);
	} else {
		return $miles;
	}
}

function main(){

	//$zipcode=getZipcode($_GET["lat"],$_GET["lng"]);

	//OPEN results file and insert into an array of lines
	$lines = file("StormFeedRestoreZoom2.json");
	
	//Merge array to STRING and decode to JSON
	$outages =json_decode(implode("",$lines));
	//$outages = json_decode(json_encode($lines));

	foreach ($outages->outages as $outage) {


		if(!is_null($outage->Cause)){
			
			$lat=$outage->lat;
			$lng=$outage->lng;
			$filter=powerout;

			$odelta=distance($_GET['olat'],$_GET['olng'],$lat,$lng, 'M');
			$delta=distance($_GET['lat'],$_GET['lng'],$lat,$lng, 'M');
			//echo ($delta." ".$_GET['rad']."\n");
			//echo ($odelta." ".$_GET['orad']."\n");
			if($delta<$_GET['rad'] && $odelta>$_GET['orad'] && (bool)strrpos($_GET['filter'],$filter)){
				$o[]=$outage;
			}

		}
/*
		

	*/	
	}

	//echo `./dohadoop.sh`;

	//BUILD response JSON
	$json['o']= $o;
	//$json['z']= $zipcode;

	echo json_encode($json);
}


//CALL MAIN
main();


//echo phpinfo();
//echo 'Last error: ', json_last_error();
?>
