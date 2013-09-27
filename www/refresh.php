<?php
header('Cache-Control: no-cache, must-revalidate');
header('Content-type: application/json');

$file_handle = fopen("results.txt", "r");
$tweets = array();

while (!feof($file_handle)) {
	//file contains json in string so must be decoded to be handled as json not string
	//substr removing \n at the end of the string
	$tweets[] = json_decode(substr(fgets($file_handle),0,-1));
}
fclose($file_handle);

//echo $tweets[10];

$json['t']= $tweets;


echo json_encode($json);
//echo $json;
//echo 'Last error: ', json_last_error();
?>
