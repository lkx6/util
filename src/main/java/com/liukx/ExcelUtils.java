package com.liukx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tembin.loms.service.impl.LogisticsOrderServiceImpl;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author zhoucx
 * @Description excel工具，支持xls,xlsx格式; xls和xlsx分别对应的HSSF和XSSF
 * @Date $ $
 **/
public class ExcelUtils {

    private static final Logger logger = LoggerFactory.getLogger(com.tembin.loms.util.ExcelUtils.class);

    /**
     * @description 写入数据到物流账单xlsx模板
     * @author liukx
     * @date 2020/7/2 0002
     */
    public static void writeDataToLogisticsBillXlsx(XSSFWorkbook workbook, Map<String, String> colAttrMapping, List dataList,String fileType,ExcelCellValueParser cellValueParser){
        XSSFSheet sheet = workbook.getSheetAt(0);
        sheet.setForceFormulaRecalculation(true);

//        XSSFCellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setAlignment(HorizontalAlignment.RIGHT);//设置此样式会导致格子丢失原来的样式

        int startRow = 0;
        Map<Integer,String> headerMap = new HashMap<>();
        Map<Integer,Integer> columnSizeMap = new HashMap<>();

        if("logisticsBill".equals(fileType)){
            Row headerRow = sheet.getRow(2);

            headerMap = getHeaderMap(headerRow,colAttrMapping,columnSizeMap);
            startRow = 3;
        }else if("logisticsRepayment".equals(fileType)) {
            //todo 还款文件模板
            startRow = 1;
        }

        //在行之间插入dataList.size()行
        sheet.shiftRows(startRow, sheet.getLastRowNum(), dataList.size());

        int rowNumber = startRow;
        for (Object data : dataList) {
            JSONObject jsonMap = (JSONObject) JSON.toJSON(data);
            XSSFRow row = sheet.createRow(rowNumber);

            for (int i=0;i<headerMap.size();i++){
                Cell cell = row.createCell(i);

                Object cellValue = jsonMap.get(headerMap.get(i));
                CellType cellType = null;

                KeyValue<CellType,Object> keyValue = null;
                if(cellValueParser != null){
                    keyValue = cellValueParser.parseCellType(cellValue);
                }

                if(keyValue != null){
                    //不执行逻辑
                }else if (cellValue instanceof BigDecimal) {
                    cellType = CellType.NUMERIC;
                    cellValue = ((BigDecimal) cellValue).doubleValue();
                }else if(cellValue instanceof Date){
                    cellType = CellType.STRING;
                    cellValue = AppUtil.formatDate((Date) cellValue);
                } else {
                    cellType = CellType.STRING;
                    cellValue = (String) cellValue;
                }

                setCellValue(cell,cellType,cellValue);

                // 解决自动设置列宽中文失效的问题
                if(cellType == CellType.STRING && StringUtils.isNotEmpty((String) cellValue)){
                    columnSizeMap.put(i,Math.max(((String) cellValue).getBytes().length,columnSizeMap.getOrDefault(i,0)));
                }
            }
            rowNumber++;
        }

        //设置列宽,参考 https://github.com/Demo-Liu/MyUtils/blob/master/ExcelUtil.java
        for (Integer c : columnSizeMap.keySet()) {
            int ss = columnSizeMap.get(c)*266;
            ss = ss>30000 ? 30000 : ss;
            sheet.setColumnWidth(c,Math.max(ss,sheet.getColumnWidth(c)));
        }

        int lastRowNum = sheet.getLastRowNum();
        if ("logisticsBill".equals(fileType)) {
            //物流客户对账单汇总行
            XSSFRow sumRow = sheet.getRow(lastRowNum);
            for (int c = 7; c < 21; c++) {
                String colString = CellReference.convertNumToColString(c);
                String formula = String.format("SUM(%s4:%s%s)",colString,colString,lastRowNum);
                Cell cell = sumRow.getCell(c);
                cell.setCellFormula(formula);
                cell.getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
            }
        }
    }

    /**
     * HSSFWorkbook:是操作Excel2003以前（包括2003）的版本，扩展名是.xls；
     * XSSFWorkbook:是操作Excel2007后的版本，扩展名是.xlsx；
     * SXSSFWorkbook:是操作Excel2007后的版本，扩展名是.xlsx；
     * @description https://blog.csdn.net/sinat_29279767/article/details/86578658
     * @author liukx
     * @date 2020/7/1 0001
     */
    public static <T>List<T> readDataFromLogisticsTemplateXls(HSSFWorkbook workbook, Map<String, String> colAttrMapping, Class<T> returnObjectType, ExcelCellValueParser cellValueParser){

        HSSFSheet sheet = workbook.getSheetAt(0);
        int headerRowNumber = 0;

        for (Cell cell : sheet.getRow(headerRowNumber)) {
            if (cell.getStringCellValue().contains("单价（")) {
                cell.setCellValue(LogisticsOrderServiceImpl.applyInfoTemplatePriceHeaderKey);
            }
        }

        JSONArray jsonArray = readDataFromSheet(workbook,workbook.getSheetAt(0),headerRowNumber,colAttrMapping,cellValueParser);
        return jsonArray.toJavaList(returnObjectType);
    }

    public static Map<Integer,String> getHeaderMap(Row headerRow, Map<String, String> colAttrMapping, Map<Integer, Integer> columnSizeMap){
        Map<Integer,String> headerMap = new HashMap<>(colAttrMapping.size());
        short headerCols = headerRow.getLastCellNum();
        for (int i = 0; i < headerCols; i++) {
            Cell headerCell = headerRow.getCell(i);
            String headerCellValue = headerCell.getStringCellValue();
            if(StringUtils.isNotEmpty(headerCellValue)){
                columnSizeMap.put(i,Math.max(headerCellValue.getBytes().length,columnSizeMap.getOrDefault(i,0)));
                String value = colAttrMapping.get(headerCellValue);
                if(StringUtils.isNotEmpty(value)){
                    headerMap.put(i,value);
                }
            }else {
                //todo 123
            }
        }

        if (headerMap.size() != colAttrMapping.size()) {
            throw new IllegalArgumentException("表格格式不正确");
        }
        return headerMap;
    }

    public static JSONArray readDataFromSheet(HSSFWorkbook workbook, HSSFSheet sheet, int headerRowNumber, Map<String, String> colAttrMapping, ExcelCellValueParser cellValueParser){
        Row headerRow = sheet.getRow(headerRowNumber);
        if (headerRow == null) {
            throw new ServiceException("标题行为空");
        }

        Map<Integer,String> headerMap = getHeaderMap(headerRow,colAttrMapping, new HashMap<>());

        int totalRows = sheet.getLastRowNum();

        JSONArray jsonArray = new JSONArray(totalRows);

        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        for (int i = headerRowNumber + 1; i <= totalRows; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            JSONObject jsonObject = new JSONObject();
            for (int j=0;j<row.getLastCellNum();j++) {
                Object value = getCellValue(evaluator,row.getCell(j),cellValueParser);
                jsonObject.put(headerMap.get(i),value);
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    public static Object getCellValue(FormulaEvaluator evaluator, Cell cell, ExcelCellValueParser cellValueParser){
        cell = evaluator.evaluateInCell(cell);
        if(cellValueParser != null){
            Object value = cellValueParser.parseCellValue(cell);
            if(value != null){
                return value;
            }
        }

        if (cell == null) {
            return null;
        }

        Object value;
        switch (cell.getCellTypeEnum()) {
            case FORMULA:
                throw new ServiceException(String.format("不支持带公式的Excel导入，提示：第%s行%s列", cell.getRow().getRowNum() + 1, cell.getColumnIndex() + 1));
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue();
                } else {
                    value = new BigDecimal(new Double(cell.getNumericCellValue()).toString()).toPlainString();
                }
                break;
            default:
                value = null;
        }
        return value;
    }

    /**
     * 把图片写入表格，支持jpg,jpeg,png
     *
     * @param workbook
     * @param sheet
     * @param img  图片文件
     */
    public static void writeImage(Workbook workbook, Sheet sheet, File img,final HSSFCell cell) {
        String suffix = StringUtils.substringAfterLast(img.getName(),".").toLowerCase();
        int picIndex;
        if ("png".equals(suffix)) {
            picIndex = Workbook.PICTURE_TYPE_PNG;
        } else {
            picIndex = Workbook.PICTURE_TYPE_JPEG;
        }

        FileInputStream in = null;

        try {
            in = new FileInputStream(img);
            byte[] data = IOUtils.toByteArray(in);
            int pictureIdx = workbook.addPicture(data, picIndex);

            CreationHelper creationHelper = workbook.getCreationHelper();
            Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
            ClientAnchor clientAnchor = creationHelper.createClientAnchor();
            //设置图片在表格中的定位
            clientAnchor.setRow1(cell.getRowIndex());
            clientAnchor.setRow2(cell.getRowIndex());
            clientAnchor.setCol1(cell.getColumnIndex());
            clientAnchor.setCol2(cell.getColumnIndex());

            Picture picture = drawingPatriarch.createPicture(clientAnchor, pictureIdx);
            picture.resize(0.8, 0.9);
        } catch (Exception e) {
            logger.error("图片写入到excel失败,e:{}", e);
        } finally {
            IOUtils.closeQuietly(in);
        }

    }

    /**
     * 从表格中读取图片到file中
     *
     * @param picture
     * @param file
     */
    public static void readImage(Picture picture, File file) {
        PictureData pdata = picture.getPictureData();
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(pdata.getData());
        } catch (Exception e) {
            logger.error("读取excel文件的图片失败,e:{}", e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error("输出流关闭失败");
                }
            }
        }
    }

    /**
     * @description todo 待完善
     * @author liukx
     * @date 2020/7/8 0008
     */
    public static void setCellValue(Cell cell,CellType cellType,Object cellValue){
        if(cellType == CellType.NUMERIC){
            cell.setCellValue((Double) cellValue);
        }else{
            cell.setCellValue((String) cellValue);
        }
    }

    /**
     * @description 自定义解析excel单元格
     * @author liukx
     * @date 2020/7/1 0001
     */
    public interface ExcelCellValueParser{

        /**
         * @description 返回空,解析失败,返回非空,解析成功
         * @author liukx
         * @date 2020/7/1 0001
         */
        default Object parseCellValue(Cell cell){
            return null;
        }

        /**
         * @description 返回空,没有解析出来,走原来的逻辑,返回非空,解析成功
         * @author liukx
         * @date 2020/7/8 0008
         */
        default KeyValue<CellType,Object> parseCellType(Object cellValue){
            return null;
        }
    }



}
