package folder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timeSum.Job;

public class Folder_File_Result2 {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//读取具体文件每一行的内容
		String content;
		//Job对象的list
		ArrayList<Job> JobObjecttList = new ArrayList<Job>();
		//需要分析文件所在的文件夹
		String filePath ="C:\\Users\\LeiMao\\Desktop\\log\\ジョブ実行結果_新\\日次処理\\";
//		String filePath =args[0];
        File file = new File(filePath);
        //判断文件或目录是否存在
        if(!file.exists()){
        	System.out.println("【"+filePath + " not exists】");
            
        }
        //获取该文件夹下所有的文件
        File[] fileArray= file.listFiles();
        File fileName = null;
        
        for(int i =0;i<fileArray.length;i++){
            fileName = fileArray[i];
            //判断此文件是否存在
            if(fileName.isDirectory()){
            	System.out.println("【目录："+fileName.getName()+"】");
                
            }else{
            	BufferedReader bfr = new BufferedReader(new FileReader(filePath+fileName.getName()));
            	Job j = new Job();
            	//正序读取，并将文件名，开始时间放入对象
            	while((content = bfr.readLine()) != null) {
            		//正则表达式匹配到符合时间格式的那一行
        			if(content.matches(".*\\d{4}\\/\\d{2}(\\/\\d{2}).*")) {
        				//正则表达式匹配到符合时间格式的字符串
        				Pattern pt=Pattern.compile("\\d{4}\\/\\d{2}(\\/\\d{2})");
        				Matcher mt=pt.matcher(content);
        				while(mt.find()) {
        					int a = mt.start()+11;
//        					System.out.println(content.substring(a,a+12));
        					
        					j.setJobId(fileName.getName());
        					//截取这一行的时分秒的字符串，并保存到Job对象中
        					j.setJobTimeS(content.substring(a,a+12));
        					break;
        				}
        				break;
        			}
        		}
            	//倒序读取，并将结束时间放入对象
            	readReverse(j,filePath+fileName.getName(),"SJIS");
            	JobObjecttList.add(j);
            	bfr.close();
            	
//            	System.out.println(fileName.getName());
            }
        }
        //写结果文件
        BufferedWriter bfw = new BufferedWriter(new FileWriter("C:\\Users\\LeiMao\\Desktop\\新_日次処理_result.txt"));
//        BufferedWriter bfw = new BufferedWriter(new FileWriter(args[1]));
        //头部标题
        String contentResultStr ="JobID        	JobStartTime	JobEndTime	TimeDiff";
        bfw.write(contentResultStr);
        bfw.write("\n");
        String contentResult ="";
        int timeDiff = 0;
        //循环输出Job对象的list中的内容
        for(Job j:JobObjecttList) {
        	System.out.println(j.getJobId() + "  "+j.getJobTimeS()+"  "+j.getJobTimeE());
        	
        	timeDiff = timeCount(j.getJobTimeS(),j.getJobTimeE());
        	contentResult = j.getJobId()+"	"+j.getJobTimeS()+"	"+j.getJobTimeE()+"	"+timeDiff;
			bfw.write(contentResult);
			bfw.write("\n");
        }
        bfw.close();
	}
	
	/*
	 * //倒序读取，并将结束时间放入对象
	 * */
	public static void readReverse(Job j,String filename, String charset) {
        RandomAccessFile rf = null;
        try {
        	String content;
            rf = new RandomAccessFile(filename, "r");
            long fileLength = rf.length();
            long start = rf.getFilePointer();// 返回此文件中的当前偏移量
            long readIndex = start + fileLength -1;
            String line;
            rf.seek(readIndex);// 设置偏移量为文件末尾
            int c = -1;
            while (readIndex > start) {
                c = rf.read();
                if (c == '\n' || c == '\r') {
                    line = rf.readLine();
                    if (line != null) {
                    	content = new String(line.getBytes("ISO-8859-1"),charset);
                    	if(content.matches(".*\\d{4}\\/\\d{2}(\\/\\d{2}).*")) {
            				Pattern pt=Pattern.compile("\\d{4}\\/\\d{2}(\\/\\d{2})");
            				Matcher mt=pt.matcher(content);
            				while(mt.find()) {
            					int a = mt.start()+11;
//            					System.out.println(content.substring(a,a+12));
            					j.setJobTimeE(content.substring(a,a+12));
            					break;
            				}
            				break;
            			}
//                        System.out.println(new String(line.getBytes("ISO-8859-1"),charset));
                    } else {
//                        System.out.println(line);
                    }
                    readIndex--;
                }
                readIndex--;
                rf.seek(readIndex);
//                if (readIndex == 0) {// 当文件指针退至文件开始处，输出第一行
//                    System.out.println(rf.readLine());
//                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rf != null)
                    rf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	/*
	 * 计算每个job的开始结束时间差
	 * */
	public static int timeCount(String timeStr,String timeEnd) {
		int timeStr_Hours = Integer.parseInt(timeStr.substring(0, 2));
		int timeEnd_Hours = Integer.parseInt(timeEnd.substring(0, 2));
		int timeStr_mins = Integer.parseInt(timeStr.substring(3, 5));
		int timeEnd_mins = Integer.parseInt(timeEnd.substring(3, 5));
		int timeStr_seconds = Integer.parseInt(timeStr.substring(6, 8));
		int timeEnd_seconds = Integer.parseInt(timeEnd.substring(6, 8));
		int result = (timeEnd_Hours-timeStr_Hours)*3600
				+(timeEnd_mins-timeStr_mins)*60
				+(timeEnd_seconds-timeStr_seconds);
		return result;
	}

}
