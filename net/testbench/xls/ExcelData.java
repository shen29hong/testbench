/**
 * 
 */
package net.testbench.xls;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.testbench.utility.TestBenchUtilities;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.format.CellFormatResult;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;



/**
 * @author HONSHEN
 *
 */
public class ExcelData {

//	private boolean gotBounds;
	protected int firstColumn;
	protected int endColumn;
	protected int firstRow;
	protected int endRow;
	private String[][] sheetData;
	protected Workbook workbook = null;
	protected Sheet sheet = null;
	
	public ExcelData() { // default to get the first sheet


	}

	
	public ExcelData(String path) { // default to get the first sheet
		try {
			this.workbook = WorkbookFactory.create(new FileInputStream(path));
		} catch (IOException e) {
			System.out.println("Unable to find the file '" + path + "'. Exiting test.");
			System.exit(1);
		} catch (InvalidFormatException e){
	         throw new IllegalArgumentException("Cannot create workbook from stream", e);
	    }
		
		this.sheet = workbook.getSheetAt(0);// default to get the first sheet
		ensureColumnBounds(this.sheet);

	}

	public ExcelData(String path, String sheetName){
		try {
			this.workbook = WorkbookFactory.create(new FileInputStream(path));
		} catch (IOException e) {
			System.out.println("Unable to find the file '" + path + "'. Exiting test.");
			System.exit(1);
		} catch (InvalidFormatException e){
	         throw new IllegalArgumentException("Cannot create workbook from stream", e);
	    }
		this.sheet = workbook.getSheet(sheetName);
		ensureColumnBounds(this.sheet);
	}

	public void loadDataFile(final String filePathAndName, final String sheetName) {
		try {
			this.workbook = WorkbookFactory.create(new FileInputStream(filePathAndName));
		} catch (IOException e) {
			System.out.println("Unable to find the file '" + filePathAndName + "'. Exiting test.");
			System.exit(1);
		} catch (InvalidFormatException e){
	         throw new IllegalArgumentException("Cannot create workbook from stream", e);
	    }
		this.sheet = workbook.getSheet(sheetName);
		ensureColumnBounds(this.sheet);
		
	}
	public void setWorksheetByNumber(int sheetNumber) {
		this.sheet = workbook.getSheetAt(sheetNumber);
		ensureColumnBounds(this.sheet);

	}
	
	public void setWorksheetByName(String sheetName) {
		this.sheet = workbook.getSheet(sheetName);
		ensureColumnBounds(this.sheet);

	}
	
	public String getSheetNameBySheetNumber(int sheetNumber) {
		this.sheet = workbook.getSheetAt(sheetNumber);
		ensureColumnBounds(this.sheet);
		return sheet.getSheetName();
	}
	
	
	public ArrayList<String> getValuesBySheetNumberAndColumnName(int sheetNumber, String columnName){
	
		ArrayList<String> valueList = new ArrayList<String>();
		this.sheet = workbook.getSheetAt(sheetNumber);
		ensureColumnBounds(this.sheet);
		
		int columnNumber = 0;
		
		Iterator<Row> rows = this.sheet.rowIterator();
		Row firstRow = rows.next();
		
		for (int col = this.firstColumn; col < this.endColumn; col++ ) {
			Cell cell = firstRow.getCell(col);

			String cellContent = getCellContent(cell);
			if (columnName.equalsIgnoreCase(cellContent)) {
				columnNumber = col;
				break;
			}
		}

        while (rows.hasNext()) {
            Row row = rows.next();
			Cell cell = row.getCell(columnNumber);
 
			String cellContent = getCellContent(cell);
			valueList.add(cellContent);
        }        

        return valueList;
	}

	
	public String getValueBySheetNumberAndColumnNumber(int sheetNumber, int columnNumber, String rowName){
		this.sheet = this.workbook.getSheetAt(sheetNumber);
		ensureColumnBounds(this.sheet);
		
		if ( columnNumber < this.firstColumn || columnNumber > this.endColumn ) {
			return "incorrect [" +  columnNumber +"] value";
		}
		
		Iterator<Row> rows = this.sheet.rowIterator();
		
        while (rows.hasNext()) {
            Row row = rows.next();
			Cell cell = row.getCell(0);
			String cellContent = getCellContent(cell);
			if (rowName.equalsIgnoreCase(cellContent)) {
				cell = row.getCell(columnNumber);
				String value = getCellContent(cell);
				return value;				
			}
        }

		return "";
	}
	
	public String getValueBySheetNameAndColumnNumber(String sheetName, int columnNumber, String rowName){
		this.sheet = this.workbook.getSheet(sheetName);
		ensureColumnBounds(this.sheet);
		
		if ( columnNumber < this.firstColumn || columnNumber > this.endColumn ) {
			return "incorrect [" +  columnNumber +"]  value";
		}
		
		Iterator<Row> rows = this.sheet.rowIterator();
		
        while (rows.hasNext()) {
            Row row = rows.next();
			Cell cell = row.getCell(0);
			String cellContent = getCellContent(cell);
			if (rowName.equalsIgnoreCase(cellContent)) {
				cell = row.getCell(columnNumber);
				String value = getCellContent(cell);
				return value;
				
			}
        }

		return "";	
	}

	public ArrayList<String> getValuesBySheetNumberAndColumnNumber(int sheetNumber, int columnNumber, String rowContent){
		ArrayList<String> valueList = new ArrayList<String>();
		this.sheet = workbook.getSheetAt(sheetNumber);
		ensureColumnBounds(this.sheet);
		
		Iterator<Row> rows = this.sheet.rowIterator();
		
        while (rows.hasNext()) {
        	
            Row row = rows.next();
            if ( row.getRowNum() <= this.sheet.getFirstRowNum() )
            	continue; // discard the first row
            
			Cell cell = row.getCell(columnNumber);
 
			String cellContent = getCellContent(cell);
			valueList.add(cellContent);
        }        

        return valueList;
	}


	public ArrayList<String> getValuesBySheetNameAndColumnName(String sheetName, String columnName){
		
		ArrayList<String> valueList = new ArrayList<String>();
		this.sheet = workbook.getSheet(sheetName);
		ensureColumnBounds(this.sheet);
		
		int columnNumber = 0;
		
		Iterator<Row> rows = this.sheet.rowIterator();
		Row firstRow = rows.next();
		
		for (int col = firstColumn; col < endColumn; col++ ) {
			Cell cell = firstRow.getCell(col);

			String cellContent = getCellContent(cell);
			if (columnName.equalsIgnoreCase(cellContent)) {
				columnNumber = col;
				break;
			}
		}

        while (rows.hasNext()) {
            Row row = rows.next();
			Cell cell = row.getCell(columnNumber);
 
			String cellContent = getCellContent(cell);
			valueList.add(cellContent);
        }        

        return valueList;
	}

	public HashMap<String, HashMap<String, String>> getValuesBySheetNameAndColumnNames(String sheetName, String[] columnNames, final String key, final int offset){
		
		this.sheet = workbook.getSheet(sheetName);
		ensureColumnBounds(this.sheet);		
		
		HashMap<String, HashMap<String, String>> sheetRecords = new HashMap<String, HashMap<String, String>>();
		
		int keyColumnNumber = -1;
		boolean cont = true;
		
		Iterator<Row> rows = this.sheet.rowIterator();
		Row firstRow = rows.next();
		
		int endCol = endColumn - offset;
		for (int col = firstColumn; col < endCol; col++ ) {
			Cell cell = firstRow.getCell(col + offset);

			String cellContent = getCellContent(cell);
			
			if ( cellContent == null ) {
				TestBenchUtilities.sayError("no cell value: at 1st row, " + col);
				cont = false;
				continue;
			}
			
			if ( !cellContent.equalsIgnoreCase(columnNames[col])) {
				TestBenchUtilities.sayError("column header value not matched: at 1st row, " + col + " [" + cellContent + "] expecting [" + columnNames[col] + "]");
				cont = false;
				continue;
			}
			
			if ( cellContent.equalsIgnoreCase(key) ) {
				keyColumnNumber = col;
			}
		}
		
		if ( ! cont || keyColumnNumber == -1 ) {
			return null;
		}
		
		String mKey = "";
		
		while (rows.hasNext()) {
			
			HashMap<String, String> rowRecord = new HashMap<String, String>();
			Row row = rows.next();
			
			for (int col = firstColumn; col < endCol; col++ ) {
				
				Cell cell = row.getCell(col + offset);				
				String cellContent = getCellContent(cell);
				
				if ( col == keyColumnNumber ) {
					mKey = cellContent;
				} else {
					rowRecord.put(columnNames[col], cellContent);
				}				
			}
			sheetRecords.put(mKey, rowRecord);
		}

        return sheetRecords;
	}

	/**
	 * 
	 * @param sheetName
	 * @return (sheet records as an ArrayList with a set of maps. Each map is each row of the sheet
	 */
	public ArrayList<HashMap<String, String>> getValuesBySheetName(final String sheetName){
		
		this.sheet = workbook.getSheet(sheetName);
		ensureColumnBounds(this.sheet);		
		
		ArrayList<HashMap<String, String>> sheetRecords = new ArrayList<HashMap<String, String>>();
				
		Iterator<Row> rows = this.sheet.rowIterator();
		Row firstRow = rows.next();
		ArrayList<String> columnNames = new ArrayList<String>();
		
		for (int col = firstColumn; col < endColumn; col++ ) {
			Cell cell = firstRow.getCell(col);

			String cellContent = getCellContent(cell);
			
			if ( cellContent == null || cellContent.isEmpty() ) {
				TestBenchUtilities.sayError("no cell value: at 1st row, " + col);
				continue;
			}
			
			columnNames.add(cellContent);			
		}
				
		while (rows.hasNext()) {
			
			HashMap<String, String> rowRecord = new HashMap<String, String>();
			Row row = rows.next();
			
			for (int col = firstColumn; col < endColumn; col++ ) {
				
				Cell cell = row.getCell(col);				
				String cellContent = getCellContent(cell);
				
				if ( cellContent == null || (cellContent.isEmpty() && col == firstColumn) ) {
					continue; // skip it
				}
				
				if ( cellContent == null || cellContent.isEmpty() ) {
					cellContent = "no value";
				} 
				
				rowRecord.put(columnNames.get(col), cellContent);
							
			}
			
			sheetRecords.add(rowRecord);
		}

        return sheetRecords;
	}

	public int getColumnNumberByColumnName(final String columnName){
		
//		this.sheet = workbook.getSheet(sheetName);
//		ensureColumnBounds(this.sheet);
		
		int columnNumber = 0;
		
		Iterator<Row> rows = this.sheet.rowIterator();
		Row firstRow = rows.next();
		
		for (int col = firstColumn; col < endColumn; col++ ) {
			Cell cell = firstRow.getCell(col);

			String cellContent = getCellContent(cell);
			if (columnName.equalsIgnoreCase(cellContent)) {
				columnNumber = col;
				break;
			}
		}

        return columnNumber;
	}

	public String getValuesBySheetNameAndRowName(final String sheetName, final String rowName){
	
		this.sheet = workbook.getSheet(sheetName);
		ensureColumnBounds(this.sheet);
		
		Iterator<Row> rows = this.sheet.rowIterator();
		Row firstRow = rows.next();
		
		for (int col = firstColumn; col < endColumn; col++ ) {
			Cell cell = firstRow.getCell(col);

			String cellContent = getCellContent(cell);
			if (rowName.equalsIgnoreCase(cellContent)) {
				
				cell = firstRow.getCell(col+1);
				cellContent = getCellContent(cell);
				return cellContent;
			}
		}

        return "";
	}
	
	public String getValueBySheetNumber(int sheetNumber, String columnName){
		this.sheet = this.workbook.getSheetAt(sheetNumber);
		ensureColumnBounds(this.sheet);
		
		int columnNumber = 0;
		
		Iterator<Row> rows = this.sheet.rowIterator();
		Row firstRow = rows.next();
		
		for (int col = firstColumn; col < endColumn; col++ ) {
			Cell cell = firstRow.getCell(col);

			String cellContent = getCellContent(cell);
			if (columnName.equalsIgnoreCase(cellContent)) {
				columnNumber = col;
				break;
			}
		}
		
        Row row = rows.next();
		Cell cell = row.getCell(columnNumber);

		String cellContent = getCellContent(cell);		
		
		return cellContent;
	}	
	
	public int getNumberOfColumnsBySheetName(String sheetName) {
		this.sheet = this.workbook.getSheet(sheetName);
		ensureColumnBounds(this.sheet);

		return this.endColumn;
	}

	public int getNumberOfColumnsBySheetNumber(int sheetNumber) {
		this.sheet = this.workbook.getSheetAt(sheetNumber);
		ensureColumnBounds(this.sheet);

		return this.endColumn;
	}

	public int getNumberOfRowsBySheetName(String sheetName) {
		this.sheet = this.workbook.getSheet(sheetName);
		ensureColumnBounds(this.sheet);

		return this.endRow;		
	}
	
	public void setValuesBySheetName(String sheetName) {
		this.sheet = this.workbook.getSheet(sheetName);
		ensureColumnBounds(this.sheet);
		// col, row
		sheetData= new String[this.endColumn][this.endRow];
		for (int j=firstRow; j<endRow; j++) {
			Row row = sheet.getRow(j);
			for(int i=firstColumn; i<endColumn; i++) {
				Cell cell = row.getCell(i);
				String content = getCellContent(cell);
				sheetData[i][j] = content;
			}

		}		
	}
	
	public String getValueByColumnAndRow(int columnIndex, int rowIndex) {
		Row row = this.sheet.getRow(rowIndex);
		Cell cell = row.getCell(columnIndex);
		String content = getCellContent(cell);
		return content;
	}
	
	/** 
	 * Gets the value of the supplied column from the current row of the data sheet.
	 * This method stops looking through columns when it comes across an empty column.
	 * 
	 * @param column - The name of the column where the data is located.
	 * @return - The data retrieved.
	 */
	public String getValue(String columnName){

		Row row = this.sheet.getRow(0);
		
		for (int col = this.firstColumn; col < this.endColumn; col++) {
			Cell cell = row.getCell(col);
			String content = getCellContent(cell);
			
			if (columnName.equalsIgnoreCase(content)){
				row = this.sheet.getRow(1);
				cell = row.getCell(col);
				String value = getCellContent(cell);
				return value;				
			} 
		}
		
		return "";
	}

	public String getColumnValueBySheetName(String sheetName, String columnName){
		this.sheet = workbook.getSheet(sheetName);
		ensureColumnBounds(this.sheet);
		
		Row row = this.sheet.getRow(0);
		
		for (int col = this.firstColumn; col < this.endColumn; col++) {
			Cell cell = row.getCell(col);
			String content = getCellContent(cell);
			
			if (columnName.equalsIgnoreCase(content)){
				row = this.sheet.getRow(1);
				cell = row.getCell(col);
				String value = getCellContent(cell);
				return value;				
			} 
		}
		
		return "";
	}

	public String sanitize(String val) { 

		if (val == null ) { 
			return "" ; 
		}

		return val.replaceAll( "[^\\p{L}\\p{N}\\s]" , "" ).replaceAll( "\\s+" , "-" ); 
	}
	
	protected final String getCellContent(Cell cell) {
        CellStyle style = null;
        if (cell != null) {
            style = cell.getCellStyle();
           
            //Set the value that is rendered for the cell
            //also applies the format
            CellFormat cf = CellFormat.getInstance(
                    style.getDataFormatString());
            CellFormatResult result = cf.apply(cell);
            String content = result.text;
        
            return content;
        }
        return "";
	}

	protected final void ensureColumnBounds(Sheet sheet) {
		
		Iterator<Row> iter = sheet.rowIterator();
		this.firstColumn = (iter.hasNext() ? Integer.MAX_VALUE : 0);
		this.endColumn = 0;
		while (iter.hasNext()) {
		    Row row = iter.next();
		    short firstCell = row.getFirstCellNum();
		    if (firstCell >= 0) {
		        this.firstColumn = Math.min(this.firstColumn, firstCell);
		        this.endColumn = Math.max(this.endColumn, row.getLastCellNum());
		    }
		}
		this.firstRow = sheet.getFirstRowNum();
		this.endRow = sheet.getLastRowNum();

	}
}
