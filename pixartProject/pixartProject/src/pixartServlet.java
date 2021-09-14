
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Servlet implementation class pixartServlet
 */
public class pixartServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	static List<String> pairsList = new ArrayList<String>(Arrays.asList("az", "by", "cx", "dw", "ev", "fu", "gt", "hs", "ir", "jq", "kp", "lo", "mn"));	//Lista delle coppie di caratteri
	private static String PATH = "C:\\Users\\miloo\\Test\\";
	
	@Override
	public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
		String mappa = "";

		httpServletRequest.setCharacterEncoding("UTF-8");
		httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		httpServletResponse.setHeader("Pragma", "no-cache");
		httpServletResponse.setDateHeader("Expires", 0);
		Enumeration<String> stringEnumeration = httpServletRequest.getParameterNames();
		String chiave = "";
		while (stringEnumeration.hasMoreElements()) {
			chiave = stringEnumeration.nextElement().toString();
			System.out.println(chiave + "=" + httpServletRequest.getParameter(chiave.toString()));
		}
		mappa = httpServletRequest.getParameter("MAP");
		if (mappa.equals("BRACKETS")) {
			String inputArray = httpServletRequest.getParameter("ARRAYINPUT");
			List<String> inputList = new ArrayList<String>(Arrays.asList(inputArray.split(",")));
			List<String> outputList = cleanExternalBrackets(inputList);
			Gson gson = new Gson();
			httpServletResponse.setContentType("application/json");
			PrintWriter printWriter = httpServletResponse.getWriter();
			printWriter.print(gson.toJson(outputList));
			printWriter.flush();
			printWriter.close();
		} else if (mappa.equals("PAIRSEN")) {
			String inputArray = httpServletRequest.getParameter("ARRAYINPUT");
			List<String> inputList = new ArrayList<String>(Arrays.asList(inputArray.split(",")));
			List<String> outputList = cleanExternalPairsEn(inputList);
			Gson gson = new Gson();
			httpServletResponse.setContentType("application/json");
			PrintWriter printWriter = httpServletResponse.getWriter();
			printWriter.print(gson.toJson(outputList));
			printWriter.flush();
			printWriter.close();
		} else if (mappa.equals("PDF")) {
			String inputArray = httpServletRequest.getParameter("ARRAYINPUT");
			List<String> inputList = new ArrayList<String>(Arrays.asList(inputArray.split(",")));
			String outputArray = httpServletRequest.getParameter("ARRAYOUTPUT");
			List<String> outputList = new ArrayList<String>(Arrays.asList(outputArray.split(",")));
			try {
				createPDF(inputList, outputList);
			} catch (DocumentException e) {	// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {	// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {	// TODO Auto-generated catch block
				e.printStackTrace();
			}
			httpServletResponse.setContentType("application/text");
			PrintWriter printWriter = httpServletResponse.getWriter();
			printWriter.print(PATH + "download.pdf");
			printWriter.flush();
			printWriter.close();
		} else if (mappa.equals("DOWNLOAD")) {
			String FileExport = PATH + "download.pdf";
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			FileInputStream fileInputStream = new FileInputStream(FileExport);
			int Nbyte = fileInputStream.available();
			byte buffer[] = new byte[Nbyte];
			fileInputStream.read(buffer);
			byteArrayOutputStream.write(buffer);
			fileInputStream.close();
			httpServletResponse.setContentType("application/octet-stream");
			httpServletResponse.setHeader("Content-disposition", "attachment; filename=\"" + FileExport + "\"");
			httpServletResponse.setContentLength(byteArrayOutputStream.size());
			httpServletResponse.getOutputStream().write(byteArrayOutputStream.toByteArray());
			httpServletResponse.getOutputStream().flush();
			deleteFile();
		} else {
			httpServletResponse.setContentType("application/text");
			PrintWriter printWriter = httpServletResponse.getWriter();
			printWriter.print("ERROR");
			printWriter.flush();
			printWriter.close();
		}
	}
	
	/**
	 * 
	 * cleanExternalBrackets() - Funzione che estrare dalla lista di stringhe stringa alla volta per mandare alla funzione che elimina le parentesi. Ha come ritorno la lista di stringe modificate
	 * 
	 * @param inputList:List<String> - Lista di valori non trasformati
	 * @return outputList:List<String> - Lista di valori trasformati
	 * 
	 */
	public static List<String> cleanExternalBrackets(List<String> inputList) {
		List<String> outputList = new ArrayList<String>();
		for (String inputStringa : inputList) {
			outputList.add(removeFirstLastBracket(inputStringa));
		}
		return outputList;
	}
	
	/**
	 * 
	 * removeFirstLastChar() - Funzione che controlla la stringa ed elimina le parentesi finali. Ha come ritorno la stringa modificata
	 * 
	 * @param inputStringa:String - Stringa non modificata
	 * @return output:String - Stringa modificata
	 * 
	 */
	public static String removeFirstLastBracket(String inputStringa) {
		String output = null;
		String[] appList = inputStringa.split("");
		boolean flagOk = false;
		while (!flagOk) {
			if (appList[0].equals("(")) {
				if (appList[appList.length-1].equals(")")) {
					output = inputStringa.substring(1, inputStringa.length()-1);
					output = check(inputStringa, output);
					if (!output.equals(inputStringa)) {
						appList = output.split("");
						inputStringa = output;
					} else {
						output = inputStringa;
						flagOk = true;
					}
				} else {
					output = inputStringa;
					flagOk = true;
				}
			} else {
				output = inputStringa;
				flagOk = true;
			}
		}
		return output;
	}
	
	/**
	 * 
	 * check() - Funzione che controlla se la stringa senza le parentesi finali e valida o no. Ha come ritorno se la modifica è valila la stringa senza parentesi finali altrimenti la stringa originale
	 * 
	 * @param originalString:String - Stringa originale
	 * @param modifyString:String - Stringa modificata
	 * @return 
	 * 
	 */
	public static String check(String originalString, String modifyString) {
		int primo = modifyString.indexOf(")");
		int secondo = modifyString.indexOf("(");
		if (primo < secondo) {
			return originalString;
		} else {
			primo = 0;
	        for (int cont=0;cont<originalString.length();cont++) {
	            char app = originalString.charAt(cont);
	            if (app == '(') {
	            	primo++;
	            }
	        }
			secondo = 0;
	        for (int cont=0;cont<originalString.length();cont++) {
	            char app = originalString.charAt(cont);
	            if (app == ')') {
	            	secondo++;
	            }
	        }
	        if (primo == secondo) {
	        	return modifyString;
	        } else {
				return originalString;
	        }
		}
	}
	
	/**
	 * 
	 * cleanExternalPairsEn() - Funzione che estrare dalla lista di stringhe stringa alla volta per mandare alla funzione che elimina le parentesi. Ha come ritorno la lista di stringe modificate
	 * 
	 * @param inputList:List<String> - Lista di valori non trasformati
	 * @return outputList:List<String> - Lista di valori trasformati
	 * 
	 */
	public static List<String> cleanExternalPairsEn(List<String> inputList) {
		List<String> outputList = new ArrayList<String>();
		for (String inputStringa : inputList) {
			outputList.add(removeFirstLastChar(inputStringa));
		}
		return outputList;
	}
	
	/**
	 * 
	 * removeFirstLastChar() - Funzione che controlla la stringa ed elimina le coppie 
	 * 
	 * @param inputStringa:String - Stringa non modificata
	 * @return output:String - Stringa modificata
	 * 
	 */
	public static String removeFirstLastChar(String inputStringa) {
		String output = null;
		String[] appList = inputStringa.split("");
		boolean flagOk = false;
		while (!flagOk) {
			String app = appList[0] + appList[appList.length-1];
			output = checkPairs(app, inputStringa);
			if (!output.equals(inputStringa)) {
				appList = output.split("");
				inputStringa = output;
			} else {
				output = inputStringa;
				flagOk = true;
			}
		}
		return output;
	}
	
	/**
	 * 
	 * checkPairs() - Funzione che controlla dalla Lista delle coppie che la coppia preso dalla stringa e lo elimina se c'è nella lista delle coppie
	 * 
	 * @param inputCheck:String - la coppia che va a controllare
	 * @param originalString:String - la Stringa originale
	 * @return
	 * 
	 */
	public static String checkPairs(String inputCheck, String originalString) {
		for (String pair : pairsList) {
			if (pair.equals(inputCheck)) {
				return originalString.substring(1, originalString.length()-1);
			}
		}
		return originalString;
	}
	
	/**
	 * 
	 * createPDF() - Funzione che crea il PDF
	 * 
	 * @param inputList:List<String> - 
	 * @param outputList:List<String> - 
	 * 
	 * @throws DocumentException
	 * @throws URISyntaxException
	 * @throws IOException
	 * 
	 */
	public static void createPDF(List<String> inputList, List<String> outputList) throws DocumentException, URISyntaxException, IOException {
		deleteFile();
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(PATH + "download.pdf"));
		document.open();
		List<String> outputLista = creaLista(outputList);
		String[][] arrayMultidimezionale = creaArrayMultidimezionale(outputLista, outputLista.get(0).length() + 1);
		Paragraph paragrafo = creaPDFTesto(arrayMultidimezionale, outputLista.get(0).length() + 1);
		document.add(paragrafo);
		PdfPTable table = creaPDFTabella(inputList, outputList);
		document.add(table);
		document.close();
	}

	/**
	 * 
	 * creaPDFTesto() - Funzione che crea PDF - Testo
	 * 
	 * @param arrayMultidimezionale:String[][] - array multidimenzionale da trasformare in PDF
	 * @param dimenzione:int
	 * @return
	 * 
	 */
	private static Paragraph creaPDFTesto(String[][] arrayMultidimezionale, int dimenzione) {
		String stringPDF = "";
		for (int cont=0;cont<dimenzione;cont++) {			//riga
		    for (int count=0;count<dimenzione;count++) {	//colonna
		    	stringPDF += " " + arrayMultidimezionale[cont][count] + " ";
		    }
	    	stringPDF += "\n";
		}
    	stringPDF += "\n\n\n\n";
    	Paragraph paragrafo = new Paragraph(stringPDF);
		return paragrafo;
	}

	/**
	 * 
	 * creaPDFTabella() - Funzione che PDF - Tabella
	 * 
	 * @param inputList:List<String> - Lista dei valori iniziali prima della trasformazione
	 * @param outputList:List<String> - Lista dei valori trasformati
	 * @return
	 * 
	 */
	private static PdfPTable creaPDFTabella(List<String> inputList, List<String> outputList) {
		PdfPTable table = new PdfPTable(2);
		addRows(table, inputList, outputList);
		return table;
	}
	
	/**
	 * 
	 * creaLista() - Funzione crea la Lista dei valori pronti per inserire nell'array multidimenzionale
	 * 
	 * @param inputList:List<String> - Lista dei valori trasformati
	 * @return
	 * 
	 */
	private static List<String> creaLista(List<String> inputList) {
		List<String> outputList = new ArrayList<String>();
		List<String> outputListTemp = new ArrayList<String>();
		boolean flag = true;
		String appStr = "";
		int maxLunghezza = 0;
		int appDim = 0;
		for (int cont=0;cont<inputList.size();cont++) {
			if (maxLunghezza < inputList.get(cont).length()) {
				maxLunghezza = inputList.get(cont).length();
			}
		}
		for (int cont=0;cont<inputList.size();cont++) {
			appStr = inputList.get(cont);
			appDim = appStr.length();
			while (appDim < maxLunghezza) {
				if (flag) {
					appStr +=  "-";
				} else {
					appStr +=  "|";
				}
				appDim++;
			}
			if (appDim == maxLunghezza) {
				if (flag) {
					flag = false;
				} else {
					flag = true;
				}
			}
			outputListTemp.add(appStr);
		}
		flag = true;
		for (int cont=0;cont<outputListTemp.size();cont++) {
			appStr = outputListTemp.get(cont);
			int count = outputListTemp.size() - 1;
			if (cont < outputListTemp.size() - 1) {
				while (count > cont) {
					if (flag) {
						appStr +=  "-";
					} else {
						appStr +=  "|";
					}
					count--;
				}
			}
			if (flag) {
				flag = false;
			} else {
				flag = true;
			}
			outputList.add(appStr);
		}
		return outputList;
	}
	
	/**
	 * 
	 * 
	 * 
	 * @param inputList:List<String> - Lista dei valori trasformati modificati per inserire nel PDF
	 * @param dimenzione:int - dimenzione per inizializzare array multidimenzionale
	 * @return
	 * 
	 */
	private static String[][] creaArrayMultidimezionale(List<String> inputList, int dimenzione) {
		String[][] outputMultidimezionale = new String[dimenzione][dimenzione];
		for (int cont=0;cont<dimenzione;cont++) {
		    for (int count=0;count<dimenzione;count++) {
		    	outputMultidimezionale[cont][count] = " ";
		    }
		}
		int x = 0;
		int y = 0;
		int ciclo = 0;
	    for (int cont=0;cont<inputList.size();cont++) {
			String[] appStr = inputList.get(cont).split("");
			switch(ciclo) {
				case 0:		//x = 0; - y = 0;
				    for (int count=0;count<appStr.length;count++) {
				    	outputMultidimezionale[x][y] = appStr[count];
				    	y++;
				    }
				    ciclo++;
				break;
				case 1:		//y = y; y output del ciclo precedente
					x = x + 1;
				    for (int count=0;count<appStr.length;count++) {
				    	outputMultidimezionale[x][y] = appStr[count];
				    	x++;
				    }
				    ciclo++;
				break;
				case 2:		//x = x; x output del ciclo precedente
					y = y - 1;
				    for (int count=appStr.length-1;count>=0;count--) {
				    	outputMultidimezionale[x][y] = appStr[count];
				    	y--;
				    }
				    ciclo++;
				break;
				case 3:		//y = y; y output del ciclo precedente
					x = x - 1;
				    for (int count=appStr.length-1;count>=0;count--) {
				    	outputMultidimezionale[x][y] = appStr[count];
				    	x--;
				    }
				    ciclo++;
				break;
				case 4:		//x = x; x output del ciclo precedente
					y = y + 1;
				    for (int count=0;count<appStr.length;count++) {
				    	outputMultidimezionale[x][y] = appStr[count];
				    	y++;
				    }
				    ciclo = 1;
				break;
			}
		}
		return outputMultidimezionale;
	}
	
	/**
	 * 
	 * addRows() - Funzione che crea la riga del PDF
	 * 
	 * @param table: PdfPTable - 
	 * @param inputList:List<String> - 
	 * @param outputList:List<String> - 
	 * 
	 */
	private static void addRows(PdfPTable table, List<String> inputList, List<String> outputList) {
		for (int cont=0;cont<inputList.size();cont++) {
		    table.addCell(inputList.get(cont));
		    table.addCell(outputList.get(cont));
		}
	}
	
	/**
	 * 
	 * deleteFile() - Funzione che cancella il file PDF creato dopo aver scaricato oppure prima di creare un file nuovo
	 * 
	 */
	private static void deleteFile() {
		File fileFolder = new File(PATH);
		if (fileFolder.listFiles().length > 0) {
			for (final File fileEntry : fileFolder.listFiles()) {
				if (!fileEntry.isDirectory()) {
					if (fileEntry.getName().indexOf("pdf") != -1) {
						fileEntry.delete();
					}
				}
			}
		}
	}
}