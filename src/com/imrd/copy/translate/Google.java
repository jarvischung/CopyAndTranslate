package com.imrd.copy.translate;

import java.util.List;

/**
 * a model class for gson parser
 * 
 * @author weikunlu
 *
 */
public class Google {
	public List<Sentences> sentences;
	public List<Dict> dict;
	public String src;
	public String server_time;
	
	public class Sentences{
		public String trans;
		public String orig;
		public String translit;
		public String src_translit;
		
	}
	
	public class Dict{
		public String pos;
		public List<String> terms;
		public List<Entry> entry;
		
		public class Entry{
			public String word;
			public List<String> reverse_translation;
			public String score;
		}
		
	}
	
	public Google() {
	}
	
}
