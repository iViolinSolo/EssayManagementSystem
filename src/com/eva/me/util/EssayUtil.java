/**
 * 
 */
package com.eva.me.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.poi.util.TempFile;

import com.eva.me.dao.EssayDAOImpl;
import com.eva.me.lucene.LuceneIndex;
import com.eva.me.model.Essay;
import com.eva.me.model.QAPair;
import com.google.gson.Gson;

/**
 * @author violi
 *
 */
public class EssayUtil {
	public static void saveAndIndex(Essay essay) {
		
		//get json by essay
		String question = essay.getTitle();
		String answer = essay.getContent();
		
		System.out.println("question:"+question+"\nans:"+answer);

		question = decode(question);
		answer = decode(answer);
		System.out.println("question:"+question+"ans:"+answer);
		answer = answer.replace("<p>", "");
		answer = answer.replace("</p>", "");
		answer = answer.replace("\r\n", "");
		
		QAPair temp = new QAPair(question,answer);
		Gson gson = new Gson();
		String tempJson = gson.toJson(temp);
//		String tempJson = question+answer;
		Log.i("=============json:===="+tempJson);
		
		// store json file
		final String fileName = md5(question+answer);
		Log.i("========== MD5 =============="+fileName);

		String finalPath = LuceneIndex.fileDirectoryPath + File.separator;
		
		File f = new File(finalPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		finalPath+=fileName+".txt";
		
		System.out.println("filepath:"+finalPath);
		try {
			File fout = new File(finalPath);
			if (!fout.exists()) {
				fout.createNewFile();
			}
			FileOutputStream outputStream = new FileOutputStream(fout);
			outputStream.write(tempJson.getBytes());
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// add index
		LuceneIndex lIndex = new LuceneIndex();
		lIndex.addIndex(finalPath);
		
//		final String prefix = "<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><head><title>Search</title></head><body><div class=\"q\">";
//		final String middle = "<br></div><br><div class=\"a\">";
//		final String end = "</div></body></html>";
//		tempJson = prefix + question + middle + answer + end;
//		Log.i("=============html:===="+tempJson);
//
//		File file = new File(finalPath);
//		try {
//			file.createNewFile();
//			FileOutputStream outputStream = new FileOutputStream(file);
//			outputStream.write(tempJson.getBytes());
//			outputStream.flush();
//			outputStream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	}
	
	public static String md5(String str){  
	    String pwd = null;  
	    try {  
	        // 生成一个MD5加密计算摘要  
	        MessageDigest md = MessageDigest.getInstance("MD5");  
	        // 计算md5函数  
	        md.update(str.getBytes());  
	        // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符  
	        // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值  
	        pwd = new BigInteger(1, md.digest()).toString(16);  
	    } catch (NoSuchAlgorithmException e) {  
	        e.printStackTrace();  
	    }  
	    return pwd;  
	}
	
	
	
	/**
	 * 可将中文转换成 "&#" 开头的html实体编码
	 *
	 *
	 * @param str
	 * @return
	 */
	public static String encode(String str) {
	  char[] arrs = str.toCharArray();//Hex.encodeHex();
	  StringBuilder sb = new StringBuilder();
	  for (char c : arrs) {
		// \\u 表示Unicode编码。
		if (c >= '\u2E80' && c <= '\uFE4F') {//  [ 只是中文一般 [ \u4e00-\u9fa5]；中日韩统一表意文字（CJK Unified Ideographs） [\u2E80-\uFE4F]
	      sb.append("&#").append((int)c).append(";");
	    } else {
	      sb.append(c);
	    }
	  }
	  return sb.toString();
	}


	/**
	 *  "&#" 开头的html实体编码 转换成中文（其实只是将长度为5的整型作了转换，对开其它如英文实体会出现错误。）
	 *
	 * <option value="zh_CN">&#20013;&#25991; (&#31616;&#20307;)</option><option value="zh_TW">&#20013;&#25991; (&#32321;&#39636;)</option>
	 *
	 * @param str
	 * @return
	 */
	@Deprecated
	public static String decode(String str) {
	  String[] tmp = str.split(";&#|&#|;");
	  StringBuilder sb = new StringBuilder("");
	  for (int i = 0; i < tmp.length; i++) {
	    if (tmp[i].matches("\\d{5}")&&!tmp[i].equals("12366")) {//TODO: HARD CODE to implements except 12366 num
	      sb.append((char) Integer.parseInt(tmp[i]));
	    } else {
	      sb.append(tmp[i]);
	    }
	  }
	  return sb.toString();
	}
	
	
	
	
	public static void main(String[] args) {
		Essay test = new EssayDAOImpl().getEssayById(8);
//		test.setTitle("")
		saveAndIndex(test);
	}
}
