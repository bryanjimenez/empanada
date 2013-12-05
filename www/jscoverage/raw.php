<?php
//header('Expires: Sun, 20 Jan 1985 00:00:00 GMT'); // date in the past
header('Cache-Control: no-cache, must-revalidate');
header('Content-type: application/json');
//header('Pragma: no-cache');

// Report all errors except E_NOTICE
// This is the default value set in php.ini
//error_reporting(E_ALL ^ E_NOTICE);

//ini_set('display_errors', '1');

//DANGER!!
ini_set('memory_limit','-1');




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
	$lines = file("/home/jonathan/raw.txt");

	foreach ($lines as $line) {		

		$tweet 	= json_decode(str_replace("\n","",$line));

		
		if(!is_null($tweet->geo)){
			$lat=$tweet->geo->coordinates[0];
			$lng=$tweet->geo->coordinates[1];
		}

		$delta=distance($_GET['lat'],$_GET['lng'],$lat,$lng, 'M');

		if($delta<$_GET['rad']){
			$filters[] = 'raw';
			$tweets[] = $tweet;
			$ranks[] = 666;

		}

	}

	//BUILD response JSON
	$json['t']= $tweets;
	$json['f']= $filters;
	$json['r']= $ranks;

	//if($tweets)
	echo json_encode($json);
}


//CALL MAIN
main();


//echo phpinfo();
//echo 'Last error: ', json_last_error();
?>
