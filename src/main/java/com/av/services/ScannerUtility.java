package com.av.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

public class ScannerUtility {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Files.walk(Paths.get("uploads"))
			// .filter(p -> p.toString().endsWith(".ext"))
			// .map(p -> p.getParent().getParent())
			// .distinct()
			 .forEach(System.out::println);
			
			if (!Files.exists(Paths.get("uploads/AV_1234"), LinkOption.NOFOLLOW_LINKS)) {
			      Files.createDirectories(Paths.get("uploads"));

	    	}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	}

}
