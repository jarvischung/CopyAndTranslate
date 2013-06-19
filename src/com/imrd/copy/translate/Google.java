package com.imrd.copy.translate;

import java.util.List;

public class Google {
	public List<Sentences> sentences;
	public List<Dict> dict;
	public String src;
	public String server_time;
	
	public class Sentences{
		String trans;
		String orig;
		String translit;
		String src_translit;
		
	}
	
	public class Dict{
		String pos;
		List<String> terms;
		List<Entry> entry;
		
		public class Entry{
			String word;
			List<String> reverse_translation;
			String score;
		}
		
	}
	
	public Google() {
		// TODO Auto-generated constructor stub
	}
	
}
