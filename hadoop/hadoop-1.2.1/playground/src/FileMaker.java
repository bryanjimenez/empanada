package empanada;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class FileMaker {
	
	public FileMaker(){
		
	}
	
	public String readKeywordsFile(String fileName) throws JSONException, IOException{
		File outputFile = new File("possibleKeywords.txt");
		JSONObject possibleKeys;
		if (!outputFile.exists()){
			outputFile.createNewFile();		
			possibleKeys = new JSONObject("{}");
		}
		else{
			Scanner outputScan = new Scanner(outputFile);
			possibleKeys = new JSONObject(outputScan.useDelimiter("//A").next());
		}	
		
		File inputFile = new File(fileName);
		Scanner inputScan = new Scanner(inputFile);
		while (inputScan.hasNextLine()){
			String line = inputScan.nextLine();
			String category = line.substring(0,line.indexOf("_"));
			//System.out.println(category);
			String keyword = line.substring(line.indexOf("_")+1, line.indexOf("\t"));
			//System.out.println(keyword);
			int count = Integer.parseInt(line.substring(line.indexOf("\t")+1));			
			//System.out.println(count);
			if (possibleKeys.isNull(keyword)){
				//System.out.println(count);
				int rating = calculateRating(count);
				/*String jsonKeyword = "{\"" + keyword + "\":{\"category\":\"" + 
						category + "\", \"synonyms\":" + "null" + "\"value\":" + count;*/
				JSONObject jsonKeyword = new JSONObject("{\"category\":" + "\"" + 
						category + "\", \"synonyms\":" + "null" + ", \"value\":" + count + ", \"rating\":" + rating + "}");		
				possibleKeys.put(keyword, jsonKeyword); 	
				//System.out.println(possibleKeys.toString());
			}
			else{
				JSONObject newKeyword = possibleKeys.getJSONObject(keyword);
				String categories = newKeyword.getString("category");
				if (!categories.contains(category)){
					categories = newKeyword.getString("category") + " "+ category;
				}
				//String categories = newKeyword.getString("category") + " "+ category;
				int value = newKeyword.getInt("value") + count;
				//System.out.println(value);
				int rating = calculateRating(value);
				newKeyword.put("category", categories);
				newKeyword.put("value", value);
				newKeyword.put("rating", rating);
				possibleKeys.put(keyword, newKeyword);
				//System.out.println(keyword + newKeyword);
				//System.out.println(newKeyword.toString());
				//System.out.println(possibleKeys.toString());
			}
		}
		
		//FileWriter fileWriter = new FileWriter(outputFile);
		PrintWriter printWriter = new PrintWriter(outputFile);
		printWriter.write(possibleKeys.toString());
		printWriter.close();
		
		return possibleKeys.toString();
	}
	
	private int calculateRating(int value){
		if (value < 5)
			return 0;
		else if (value < 100)
			return 1;
		else if (value < 200)
			return 2;
		else if (value < 300)
			return 3;
		else if (value < 400)
			return 4;
		else
			return 5;
	}
	
	public String readBigramsFile(String name){
		
		return null;	
	}
	
	public void createSysnonymsFile(){
		
	}
	
	public static void main(String[] args){
		FileMaker ff = new FileMaker();
		String test = null;
		try {
			test = ff.readKeywordsFile("keywords-m-00000");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
