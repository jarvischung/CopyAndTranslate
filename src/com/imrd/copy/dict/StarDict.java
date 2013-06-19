package com.imrd.copy.dict;

/*
 Copyright 2009 http://code.google.com/p/toolkits/. All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are
 met:
 * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above
 copyright notice, this list of conditions and the following
 disclaimer in the documentation and/or other materials provided
 with the distribution.
 * Neither the name of http://code.google.com/p/toolkits/ nor the names of its
 contributors may be used to endorse or promote products derived
 from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.imrd.copy.CopyAndTranslateActivity;

class Location {
	public int offset;
	public int size;
}

public class StarDict {
	/**/
	RandomAccessFile index;
	RandomAccessFile yaindex;
	DictZipFile dz;
	String dictname;
	public String last_error = "";

	public StarDict() {
		this(CopyAndTranslateActivity.defaultSDCardPath + CopyAndTranslateActivity.defaultDictName);
		//this("/data/data/com.imrd.copy/files/langdao/langdao_ec_gb");
	}

	/**
	 * 
	 * @param dictname
	 */
	public StarDict(String dictname) {
		try {
			this.dictname = dictname;
			this.index = new RandomAccessFile(dictname + ".idx", "r");
			this.dz = new DictZipFile(dictname + ".dz");
			this.yaindex = new RandomAccessFile(dictname + ".yaidx", "r");
			// this.dz.runtest();
		} catch (FileNotFoundException e) {
			last_error = e.toString();
			e.printStackTrace();
		} catch (Exception e) {
			last_error = e.toString();
			e.printStackTrace();
		}
	}

	public String getWord(int p, Location l) {
		if (l == null) {
			l = new Location();
		}
		String word = null;
		byte[] buffer = new byte[1024];
		int dataoffset = 0;
		int datasize = 0;
		int offset = 0; // the offset of the p-th word in this.index
		try {
			this.yaindex.seek(p * 4);
			int size = this.yaindex.read(buffer, 0, 4);
			if (size != 4) {
				throw new Exception("Read Index Error");
			}
			for (int i = 0; i < 4; i++) {
				offset <<= 8;
				offset |= buffer[i] & 0xff;
			}
			this.index.seek(offset);
			size = this.index.read(buffer, 0, 1024);
			for (int i = 0; i < size; i++) {
				if (buffer[i] == 0) {
					word = new String(buffer, 0, i, "UTF8");
					dataoffset = 0;
					datasize = 0;
					for (int j = i + 1; j < i + 5; j++) {
						dataoffset <<= 8;
						dataoffset |= buffer[j] & 0xff;
					}
					for (int j = i + 5; j < i + 9; j++) {
						datasize <<= 8;
						datasize |= buffer[j] & 0xff;
					}
					break;
				}
			}
			// System.out.println(datasize);
			// buffer = new byte[datasize];
			// this.dz.seek(dataoffset);
			// this.dz.read(buffer, datasize);
			l.offset = dataoffset;
			l.size = datasize;
		} catch (Exception e) {
			last_error = e.toString();
			e.printStackTrace();
		}
		return word;
	}

	/**
	 * 
	 * @param word
	 * @return the explanation of the word
	 */
	public String getExplanation(String word) {
		int i = 0;
		int max = getWordNum();
		String w = "";
		int mid = 0;
		Location l = new Location();
		String exp = null;
		// return this.dz.test()+this.dz.last_error;
		// /*
		while (i <= max) {
			mid = (i + max) / 2;
			w = getWord(mid, l);
			if (w.compareTo(word) > 0) {
				max = mid - 1;
			} else if (w.compareTo(word) < 0) {
				i = mid + 1;
			} else {
				break;
			}
		}
		// get explanation
		byte[] buffer = new byte[l.size];
		this.dz.seek(l.offset);
		try {
			this.dz.read(buffer, l.size);
		} catch (Exception e) {
			last_error = e.toString();
			buffer = null;
			exp = e.toString();
		}

		try {
			if (buffer == null) {
				exp = "Error when reading data\n" + exp;
			} else {
				exp = new String(buffer, "UTF8");
			}
		} catch (Exception e) {
			last_error = e.toString();
			e.printStackTrace();
		}
		return w + "\n" + exp;
		// */
	}

	/**
	 * 
	 * @param word
	 * @return the explanation of the word
	 */
	public String getExplanation2(String word) {
		int i = 0;
		int max = getWordNum();
		String w = "";
		int mid = 0;
		Location l = new Location();
		String exp = null;
		int cmp = 0;

		while (i <= max) {
			mid = (i + max) / 2;
			w = getWord(mid, l);
			/*
			 * use search algorithm used by stardict,otherwise we will found the
			 * wrong word, fortitude.zhang, 2011/12/26
			 */
			cmp = stardictStrcmp(w, word);
			if (cmp > 0) {
				max = mid - 1;
			} else if (cmp < 0) {
				i = mid + 1;
			} else {
				break;
			}
		}

		// get explanation
		byte[] buffer = new byte[l.size];
		this.dz.seek(l.offset);
		try {
			this.dz.read(buffer, l.size);
		} catch (Exception e) {
			last_error = e.toString();
			buffer = null;
			exp = e.toString();
		}

		try {
			if (buffer == null) {
				exp = "Error when reading data\n" + exp;
			} else {
				exp = new String(buffer, "UTF8");
			}
		} catch (Exception e) {
			last_error = e.toString();
			e.printStackTrace();
		}
		return w + "\n" + exp;
		// return mid+"\n"+l.offset+exp+l.size;
		// */
	}

	/*
	 * stardict_strcmp, we need this function gint a=g_ascii_strcasecmp(s1, s2);
	 * if (a == 0) return strcmp(s1, s2); else return a; fortitude.zhang,
	 * 2011/12/26
	 */
	private int stardictStrcmp(String str1, String str2) {
		int a;

		/*
		 * Java doc: a negative integer, zero, or a positive integer as the
		 * specified String is greater than, equal to, or less than this String,
		 * ignoring case considerations.
		 */
		a = str1.compareToIgnoreCase(str2);
		if (0 == a) {
			a = str1.compareTo(str2);
		}

		return a;
	}

	/**
	 * 
	 * @return
	 */
	public String getVersion() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(this.dictname
					+ ".ifo"));
			String line = br.readLine();
			while (line != null) {
				String[] version = line.split("=");
				if (version.length == 2 && version[0].equals("version")) {
					return version[1];
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			last_error = e.toString();
			e.printStackTrace();
		}
		return "UNKNOWN VERSION";
	}

	public int getWordNum() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(this.dictname
					+ ".ifo"));
			String line = br.readLine();
			while (line != null) {
				String[] version = line.split("=");
				if (version.length == 2 && version[0].equals("wordcount")) {
					return Integer.parseInt(version[1]);
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			last_error = e.toString();
			e.printStackTrace();
		}
		return 0;
	}
}
