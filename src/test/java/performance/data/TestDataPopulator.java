package performance.data;

import com.companyx.cabservice.resource.DriverLocation;
import com.companyx.cabservice.service.LocationService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

/**
 * Created by sr250345 on 3/27/17.
 */
public class TestDataPopulator {

    public static void populateTestData(String fileName, LocationService ls)
    {
        int recordCounter = 0;
        try
        {
            FileInputStream excelFile = new FileInputStream(new File(fileName));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            while (iterator.hasNext())
            {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                int c = 0;
                double latitude = 0;
                double longitude = 0;
                while (cellIterator.hasNext())
                {
                    Cell currentCell = cellIterator.next();
                    if(c == 0)
                    {
                        latitude = currentCell.getNumericCellValue();
                    }
                    else if(c == 1)
                    {
                        longitude = currentCell.getNumericCellValue();
                    }
                    ++c;
                }
                //System.out.println(latitude + "," + longitude);
                DriverLocation dl = new DriverLocation();
                dl.setDriverId("" + recordCounter);
                dl.setLatitude(latitude);
                dl.setLongitude(longitude);
                ls.updateLocation(dl);

                recordCounter++;

            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            System.out.println(recordCounter + " location records created");
        }
    }

    public static void main(String[] args)
    {
        String filePath = "/Users/SR250345/Documents/siddharth/personal/whereismydriver/uk_pastalcode_ locations.xlsx";
        //first column is latitude & second column is longitude
        LocationService ls = new LocationService("perftestkey");
        populateTestData(filePath, ls);
    }
}
