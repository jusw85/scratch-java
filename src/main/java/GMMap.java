import java.io.File;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;


public class GMMap {
//	public static void main(String[] args) throws Exception {
////		x="1058" y="415"
////		%1 %2 %3
//		Scanner sc = new Scanner(new File("./res/gm1.txt"));
//		String template = FileUtils.readFileToString(new File("./res/gm3.txt"), Charset.forName("utf-8"));
//		String nums = FileUtils.readFileToString(new File("./res/gm2.txt"), Charset.forName("utf-8"));
//		nums = nums.replaceAll("\r", "").replaceAll("\n", "");
//		
//		int x = 1058;
//		int y = 415;
//		int col = 0;
//		int i = 0;
//		while (sc.hasNextLine()) {
//			String inst = sc.nextLine();
//			String coords = "x=\"" + x + "\" y=\"" + y + "\"";
//			String out = template.replaceAll("%2", inst)
//					.replaceAll("%1", coords)
//					.replaceAll("%3", String.valueOf((nums.charAt(i++))));
//			System.out.println(out);
//			if (++col > 5) {
//				col = 0;
//				x = 1058;
//				y += 29;
//			} else {
//				x += 29;				
//			}
//		}
//		sc.close();
//	}
	
	public static void main(String[] args) throws Exception {
		Pattern p1 = Pattern.compile("story\\[\\d+,0\\]=(\\d+); // display page");
		Pattern p2 = Pattern.compile("story\\[\\d+,1\\]=(.*);?");
		Pattern p3 = Pattern.compile("story\\[\\d+,10\\]=(.*)\\s*;  //select A");
		Pattern p4 = Pattern.compile("story\\[\\d+,11\\]=(.*)\\s*;?.*//select A story number");
		Pattern p5 = Pattern.compile("story\\[\\d+,20\\]=(.*)\\s*;?.*//select B");
		Pattern p6 = Pattern.compile("story\\[\\d+,21\\]=(.*)\\s*;?.*//select B story number");
		Pattern p7 = Pattern.compile("story\\[\\d+,30\\]=(.*)\\s*;?.*//select C");
		Pattern p8 = Pattern.compile("story\\[\\d+,31\\]=(.*)\\s*;?.*//select C story number");
		Scanner sc = new Scanner(new File("./res/gm4.txt"));
		int i = 0;
		int start = 5;
		String page = "";
		String story = "";
		String story1 = "";
		String story1p = "";
		String story2 = "";
		String story2p = "";
		String story3 = "";
		String story3p = "";
		while (sc.hasNextLine()) {
			String line = sc.nextLine().trim();
			Matcher m1 = p1.matcher(line);
			if (m1.matches()) {
				page = m1.group(1);
			}
			Matcher m2 = p2.matcher(line);
			if (m2.matches()) {
				story = m2.group(1);
			}
			Matcher m3 = p3.matcher(line);
			if (m3.matches()) {
				story1 = m3.group(1).trim();
			}
			Matcher m4 = p4.matcher(line);
			if (m4.matches()) {
				story1p = m4.group(1).trim();
			}
			Matcher m5 = p5.matcher(line);
			if (m5.matches()) {
				story2 = m5.group(1).trim();
			}
			Matcher m6 = p6.matcher(line);
			if (m6.matches()) {
				story2p = m6.group(1).trim();
			}
			Matcher m7 = p7.matcher(line);
			if (m7.matches()) {
				story3 = m7.group(1).trim();
			}
			Matcher m8 = p8.matcher(line);
			if (m8.matches()) {
				story3p = m8.group(1).trim();
			}
			if (StringUtils.isBlank(line)) {
				System.out.println("page_map = ds_map_create()");
				System.out.println("page_map[? \"story\"] = " + story);
				if (!StringUtils.isBlank(story1)) {
					System.out.println("page_map[? \"path1\"] = " + story1);	
					System.out.println("page_map[? \"path1_page\"] = " + story1p);				
				}
//				if (!StringUtils.isBlank(story2)) {
				if (story2.length() > 2) {
					System.out.println("page_map[? \"path2\"] = " + story2);	
					System.out.println("page_map[? \"path2_page\"] = " + story2p);				
				}
//				if (!StringUtils.isBlank(story3)) {
				if (story3.length() > 2) {
					System.out.println("page_map[? \"path3\"] = " + story3);	
					System.out.println("page_map[? \"path3_page\"] = " + story3p);				
				}
				System.out.println("page_map[? \"display_page\"] = " + page);
				System.out.println("story[" + start++ + "] = page_map;");
				System.out.println();
			}
		}
		System.out.println(i);
		sc.close();
	}
}