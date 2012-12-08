package pl.mwojciec.generator.classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;

//Klasa generujaca wariacje liter i zapisujaca je do pliku
class Variation {

	//Parametry
	private int wordLength;		//Dlugosc slowa w permutacji
	private int wordCount;			//Ilosc slow
	private File file;				//Plik do ktorego zapisac dane
	private char wordEnding;		//Koncowka kazdego slowa
	
	//Zmienne pomocnicze
	private int currentGeneratedWordNumber = 0;								// Wskazuje na ilosc dotychczas wygenerowanych slow
	private LinkedList<Integer> value = new LinkedList<Integer>();				// Lista generowanych wariacji liczb calkowitych
	private LinkedList<String> generatedStrings = new LinkedList<String>();	// Lista wygenerowanych slow
	
	//Konstruktor ustawiajacy wszystkie parametry klasy
	public Variation( int wordLen, int wordCnt, File outputFile, char wordEnd ) {
		
		wordLength = wordLen;
		wordCount = wordCnt;
		file = outputFile;
		wordEnding = wordEnd;
		
	}
	
	//Funkcja generujaca string z listy intow
	private String generateString() {
		
		char[] wordArray = new char[wordLength + 1];
		int currentLetter = 0;
		
		for ( Integer i : value) {
			
			int letterAscii = 97 + i;
			wordArray[currentLetter] = (char)letterAscii;
			currentLetter++;
			
		}
		
		wordArray[wordLength] = wordEnding;
		
		return new String ( wordArray );
		
	}
	
	//Funkcja dodaje wygenerowane slowo do listy wygenerowanych slow
	private void addWord() {
		
		String word = generateString();
		generatedStrings.addLast(word);
		
	}
	
	//Funkcja zapisujaca permutacje do pliku
	private void printToFile() {
			
		try {
			PrintWriter saveFile = new PrintWriter(file);
			
			for( String s : generatedStrings ) {
				saveFile.println(s);
			}
			
			saveFile.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Nie moge otworzyc pliku do zapisu!");
			e.printStackTrace();
		}
		
	}
	
	//Funkcja generujaca wariacje
	public void variate() {	
		
		if (currentGeneratedWordNumber <= wordCount) {
			
			if (value.size() == wordLength) {

				addWord();
				currentGeneratedWordNumber++;

			} else {

				for (int i = 0; i < 26; i++) {

					value.addLast(i);
					variate();
					value.removeLast();

				}

			}
		}
		else
		{
			printToFile();
		}
		
	}
	
}
