package yelpAPI;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class WriteCSV {
	//Delimiter used in CSV file
	private static final String COMMA_DELIMITER=",";
	private static final String NEW_LINE_SEPARATOR="\n";
	private static final String PATH="./data/biz-3";
	
	//CSV file header
	private static final String FILE_HEADER="id,categories,rating,review_count";
	private static FileWriter fw;
	private static BufferedWriter br;
	
	public WriteCSV(){
		try{
			fw=new FileWriter(new File(PATH+".csv"));
			br=new BufferedWriter(fw);
		}catch(IOException e){
			System.out.println("Error in preparing CsvFileWriter !!!");
		}
		try {
			br.write(FILE_HEADER);
			br.write(NEW_LINE_SEPARATOR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in CsvFileWriter--header !!!");
		}
	}
	
	public void writeCSV(CSVobject biz){

		String id=biz.id;
		String cate=biz.categories.toString();
		char[] chararray=cate.toCharArray();
		for(int i=0;i<chararray.length;i++){
			if(chararray[i]=='\"'){
				chararray[i]=' ';
			}
			if(chararray[i]==','){
				chararray[i]=';';
			}
		}
		cate=String.valueOf(chararray);
		String rating=biz.rating;
		String reviewC=biz.reviewC;
		try {
			br.write(id);
			br.write(COMMA_DELIMITER);
			br.write(cate);
			br.write(COMMA_DELIMITER);
			br.write(rating);
			br.write(COMMA_DELIMITER);
			br.write(reviewC);
			br.write(NEW_LINE_SEPARATOR);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in CsvFileWriter !!!");
		}
		System.out.println("csv file was generated successfully!");
		
		
	}
	public void close(){
		try {
			br.flush();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in br.close() !!!");
		}
	}
}
