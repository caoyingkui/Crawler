package cn.edu.pku.EOSCN.crawler.util.htmlCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import cn.edu.pku.EOSCN.crawler.util.htmlCode.HtmlPage.Segment;
import cn.edu.pku.EOSCN.crawler.util.htmlCode.HtmlPage.Sentence;

/**
 * @ClassName: CodeJudge
 * @Description: TODO judge whether a code
 * @author: left
 * @date: 2014年3月5日 上午10:30:16
 */

public class CodeJudge {

	public static ArrayList<Segment> splitCode(Segment segment){
		ArrayList<Segment> ret = new ArrayList<Segment>();
		BufferedReader br = new BufferedReader(new StringReader(segment.getContentText()));
		int totalLOC = 0, isCodeLOC = 0;
		boolean last = false;
		ArrayList<Sentence> lastStr = new ArrayList<Sentence>(); 
		for(Sentence sent : segment.getSentences()) {
			String line = sent.getSentence();
			// only handle non-blank lines
			// System.out.println(line);
			if (line.trim().length() > 0) {
				if (hasMustOccurSymbol(line)||isCodeLine(line.trim())) {
					if (last == false){
						if (lastStr.size() > 0){
							Segment tt = new Segment();
							tt.setSentences(lastStr);
							ret.add(tt);
							lastStr = new ArrayList<Sentence>();
						}
						last = true;
					}
					lastStr.add(sent);
				}else{
					if (last == true){
						if (lastStr.size() > 0){
							Segment tt = new Segment();
							tt.setSentences(lastStr);
							ret.add(tt);
							lastStr = new ArrayList<Sentence>();
						}						
						last = false;
					}
					lastStr.add(sent);
				}
			}
		}
		Segment tt = new Segment();
		tt.setSentences(lastStr);
		ret.add(tt);
		lastStr = new ArrayList<Sentence>();
		// System.out.println(totalLOC + "/" + isCodeLOC + "/" + MUST_OCCUR +
		// "/" + ((double)isCodeLOC / ((double) totalLOC)) );
		return ret;
	}

	
	public static boolean isCode(String content) {
		BufferedReader br = new BufferedReader(new StringReader(content));
		String line = null;
		int totalLOC = 0, isCodeLOC = 0;
		boolean MUST_OCCUR = false;
		try {
			while ((line = br.readLine()) != null) {
				if (line.trim().length() > 0) {
					if ((!MUST_OCCUR) && hasMustOccurSymbol(line)) {
						MUST_OCCUR = true;
					}
					if (isCodeLine(line.trim())) {
						isCodeLOC++;
					}
					totalLOC++;
				}
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if (totalLOC > 0 && ((double)isCodeLOC / (double) totalLOC) > 0.2)
//		return true;
//		return false;
		// System.out.println(totalLOC + "/" + isCodeLOC + "/" + MUST_OCCUR +
		// "/" + ((double)isCodeLOC / ((double) totalLOC)) );
		if (totalLOC > 0 && MUST_OCCUR) {
			if ((double) isCodeLOC / ((double) totalLOC) > 0.5) {
				// System.out.println("CODE------------------");
				// System.out.println(content);
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}

	}

	public final static String[]	codeEndSymbol		= { "{", "}", ";", "=" };
	public final static String[]	codeKeywordSymbol	= { "import", "public", "private", "protected",
			"return", "package", "import"				};
	public final static String[]	mustOccurSymbol		= { "(", ")", "=", "{", "}" };

	public static boolean hasMustOccurSymbol(String line) {
		for (String s : mustOccurSymbol)
			if (line.contains(s))
				return true;
		return false;
	}

	public static boolean isCodeLine(String line) {
		//if (line.length()>100) return false;
		for (String s : codeEndSymbol) {
			if (line.endsWith(s)) {
				return true;
			}
		}
		for (String s : codeKeywordSymbol) {
			if (line.startsWith(s)) {
			//if (line.contains(s)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String args[]) {
		String path = "D:/test.txt";
		// File testFile = new File(path);
//		System.out.println(isCode(content));
		String content = "";
		System.out.println(content);

	}
}