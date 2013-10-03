<?php
header('Cache-Control: no-cache, must-revalidate');
header('Content-type: application/json');

// Report all errors except E_NOTICE
// This is the default value set in php.ini
error_reporting(E_ALL ^ E_NOTICE);

//Request would be here



//Results were sent back
$file_handle = fopen("part-r-00000", "r");
$tweets = array();

//http://stackoverflow.com/questions/9133024/www-data-permissions
//echo `./dohadoop.sh`;
/*
$pizza  = "piece1 piece2 piece3 piece4 piece5 piece6";
$pieces = explode(" ", $pizza);
echo $pieces[0]; // piece1
echo $pieces[1]; // piece2
*/

while (!feof($file_handle)) {
	//file contains json in string so must be decoded to be handled as json not string
	//substr removing \n at the end of the string
	$tweet=explode("\t",substr(fgets($file_handle),0,-1));

	$filters[] =$tweet[0];	
	$tweets[] = json_decode($tweet[1]);
	//echo explode("time",substr(fgets($file_handle),0,-1))[1];
}
fclose($file_handle);


$json['t']= $tweets;
$json['f']= $filters;
echo json_encode($json);

//echo phpinfo();
//echo $json;
//echo 'Last error: ', json_last_error();
?>
