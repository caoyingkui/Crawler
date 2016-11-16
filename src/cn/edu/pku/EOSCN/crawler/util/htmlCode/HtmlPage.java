package cn.edu.pku.EOSCN.crawler.util.htmlCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HtmlPage {
	public ArrayList<Segment> segments = new ArrayList<Segment>();
	String content;
	public HtmlPage(String content){
		this.content = content;
	}
	
	public void process(){
		(new SegmentSpliter()).process(this);
		CodeClassifier.getClassificationType(this);
	}
	
	public void merge(){
		
	}
	
	public static class Segment implements Serializable{
		private static final long serialVersionUID = -3806580258925830888L;
		public static int			NORMAL_CONTENT		= 0;
		public static int			CODE_CONTENT		= 1;
		public static int			STACK_CONTENT		= 2;
		public static int			SIGNATURE_CONTENT	= 3;
		public static int			JUNK_CONTENT		= 4;
		public static int			REF_CONTENT			= 5;

		private ArrayList<Sentence>	sentences;
		private int					contentType			= 0;

		public int getContentType() {
			return contentType;
		}

		public void setContentType(int contentType) {
			this.contentType = contentType;
		}

		public ArrayList<Sentence> getSentences() {
			return sentences;
		}

		public void setSentences(ArrayList<Sentence> sentences) {
			this.sentences = sentences;
		}

		public String getContentText() {
			StringBuilder sb = new StringBuilder();
			sentences = this.getSentences();
			for (Sentence sentence : sentences) {
				sb.append(sentence.toString() + "\n");
			}
			return sb.toString();
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("segment type : " + getContentType() + "\n");
			for (Sentence s : sentences) {
				sb.append(s.toString() + "\n");
			}
			return sb.toString();
		}
	}

	
	public static class Sentence implements Serializable{

		private static final long serialVersionUID = 3937107643827123569L;
		private String	sentence;

		public Sentence() {
			sentence = "";
		}

		public Sentence(String s) {
			this.sentence = s;
		}

		public String getSentence() {
			return sentence;
		}

		public void setSentence(String sentence) {
			this.sentence = sentence;
		}

		public String toString() {
			return sentence;
		}

	}
	
}

