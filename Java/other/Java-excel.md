
# JAVA使用POI导出海量数据附源码

- [JAVA使用POI导出海量数据附源码](https://www.jianshu.com/p/4e873e66ee73)

- ExcelUtile工具类
```java
package com.br.monitor.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author: 作者：jack-cooper
 * @explain: 释义：excel导出泛型工具类
 * @version: 日期：2016-05-31 09:59:26
 * 本工具类支持Excel导出，需要设定表头和表头对应的字段
 * 通过设定每个sheet的数据行数，可以多sheet导出
 * 支持设定时间格式
 * 支持图片导出
 * @param <T>
 */
public class ExcelUtils<T> {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);


    private static CellStyle titleStyle;        // 标题行样式
    private static Font titleFont;              // 标题行字体
    private static CellStyle dateStyle;         // 日期行样式
    private static Font dateFont;               // 日期行字体
    private static CellStyle headStyle;         // 表头行样式
    private static Font headFont;               // 表头行字体
    private static CellStyle contentStyle;      // 内容行样式
    private static Font contentFont;            // 内容行字体
    private static DataFormat format;           //内容格式化
    private static String pattern = "yyyy-MM-dd HH:mm:ss";    //默认下载日期格式

    /**
     * 处理中文文件名乱码问题
     * @param request
     * @param fileNames
     * @return
     */
    public static String processFileName(HttpServletRequest request, String fileNames) {
        String codedfilename = null;
        try {
            String agent = request.getHeader("USER-AGENT");
            if (null != agent && -1 != agent.indexOf("MSIE") || null != agent
                    && -1 != agent.indexOf("Trident")) {// ie
                String name = java.net.URLEncoder.encode(fileNames, "UTF-8");
                codedfilename = name;
            } else if (null != agent && -1 != agent.indexOf("Mozilla")) {// 火狐,chrome等
                codedfilename = new String(fileNames.getBytes("UTF-8"), "iso-8859-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codedfilename;
    }

    /**
     * @Description: 自动调整列宽
     */
    private static void adjustColumnSize(Sheet sheet, List<String> headList) {
        for (int i = 0; i < headList.size(); i++) {
            sheet.autoSizeColumn((short)i);
            sheet.setColumnWidth(i, headList.get(i).getBytes().length*2*100);
        }
    }

    /**
     * @Description: 创建统计行
     */
    private static void createCountRow(Sheet sheet, Map<String, Object> map) {
        Row countRow = sheet.createRow(0);
        Cell countCell = countRow.createCell(0);
        countCell.setCellValue("汇总信息：");
        countCell.getCellStyle().cloneStyleFrom(titleStyle);
        int num = 2;
        if(map != null) {
            for (String key : map.keySet()) {
                countCell = countRow.createCell(num);
                countCell.getCellStyle().cloneStyleFrom(titleStyle);
                countCell.setCellValue(key);
                num++;
                countCell = countRow.createCell(num);
                countCell.setCellValue(map.get(key).toString());
                countCell.getCellStyle().cloneStyleFrom(titleStyle);
                num = num + 2;
            }
        }
    }

    /**
     * @Description: 初始化标题行样式
     */
    private static void initTitleCellStyle() {
        titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        titleStyle.setFont(titleFont);
        titleStyle.setFillBackgroundColor(IndexedColors.BLACK.index);
        titleStyle.setDataFormat(format.getFormat("@"));
    }

    /**
     * @Description: 初始化数据行样式
     */
    private static void initDateCellStyle() {
        dateStyle.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
        dateStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        dateStyle.setFont(dateFont);
//        dateStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.index);
        dateStyle.setDataFormat(format.getFormat("@"));
    }

    /**
     * @Description: 初始化表头行样式
     */
    private static void initHeadCellStyle() {
        headStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        headStyle.setFont(headFont);
        //是设置单元格填充样式，SOLID_FOREGROUND纯色使用前景颜色填充，接着设置前景颜色
        headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headStyle.setFillForegroundColor(HSSFColor.LIME.index);
        headStyle.setBorderTop(CellStyle.BORDER_MEDIUM);
        headStyle.setBorderBottom(CellStyle.BORDER_THIN);
        headStyle.setBorderLeft(CellStyle.BORDER_THIN);
        headStyle.setBorderRight(CellStyle.BORDER_THIN);
        headStyle.setTopBorderColor(IndexedColors.BLACK.index);
        headStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        headStyle.setLeftBorderColor(IndexedColors.BLACK.index);
        headStyle.setRightBorderColor(IndexedColors.BLACK.index);
        headStyle.setDataFormat(format.getFormat("@"));
    }





    //============================================================辅助方法==================================================

    /**
     * @Description: 初始化内容行样式
     */
    private static void initContentCellStyle() {
        contentStyle.setAlignment(CellStyle.ALIGN_CENTER);
        contentStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        contentStyle.setFont(contentFont);
//        contentStyle.setBorderTop(CellStyle.BORDER_THIN);
//        contentStyle.setBorderBottom(CellStyle.BORDER_THIN);
//        contentStyle.setBorderLeft(CellStyle.BORDER_THIN);
//        contentStyle.setBorderRight(CellStyle.BORDER_THIN);
//        contentStyle.setTopBorderColor(IndexedColors.BLACK.index);
//        contentStyle.setBottomBorderColor(IndexedColors.BLACK.index);
//        contentStyle.setLeftBorderColor(IndexedColors.BLACK.index);
//        contentStyle.setRightBorderColor(IndexedColors.BLACK.index);
        contentStyle.setWrapText(false); // 字段换行
    }

    /**
     * @Description: 初始化标题行字体
     */
    private static void initTitleFont() {
        titleFont.setFontName("华文楷体");
        titleFont.setFontHeightInPoints((short) 10);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        titleFont.setCharSet(Font.DEFAULT_CHARSET);
        titleFont.setColor(IndexedColors.BLACK.index);
    }

    /**
     * @Description: 初始化日期行字体
     */
    private static void initDateFont() {
        dateFont.setFontName("隶书");
        dateFont.setFontHeightInPoints((short) 10);
        dateFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        dateFont.setCharSet(Font.DEFAULT_CHARSET);
        dateFont.setColor(IndexedColors.BLACK.index);
    }

    /**
     * @Description: 初始化表头行字体
     */
    private static void initHeadFont() {
        headFont.setFontName("宋体");
        headFont.setFontHeightInPoints((short) 10);
        headFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headFont.setCharSet(Font.DEFAULT_CHARSET);
        headFont.setColor(IndexedColors.BLACK.index);
    }

    /**
     * @Description: 初始化内容行字体
     */
    private static void initContentFont() {
//        contentFont.setFontName("宋体");
//        contentFont.setFontHeightInPoints((short) 10);
//        contentFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
//        contentFont.setCharSet(Font.DEFAULT_CHARSET);
//        contentFont.setColor(IndexedColors.BLACK.index);
    }

    /**
     * @Description: 初始化
     */
    public SXSSFWorkbook init() {
        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        wb.setCompressTempFiles(true);
        titleFont = wb.createFont();
        titleStyle = wb.createCellStyle();
        dateStyle = wb.createCellStyle();
        dateFont = wb.createFont();
        headStyle = wb.createCellStyle();
        headFont = wb.createFont();
        contentStyle = wb.createCellStyle();
        contentFont = wb.createFont();
        format = wb.createDataFormat();
        initTitleCellStyle();
        initTitleFont();
        initDateCellStyle();
        initDateFont();
        initHeadCellStyle();
        initHeadFont();
        initContentCellStyle();
        initContentFont();
        return wb;
    }

    /**
     * 说明：支持海量数据下载
     * @param workbook 工作薄
     * @param heads sheet表头【数组】
     * @param columns 表头对应的对象属性【数组】
     * @param statisticsMap 统计信息
     * @param excelName excel名字
     * @param result 结果集
     * @param sheetNum sheet编号
     * @return 工作薄
     * @throws Exception
     */
    public SXSSFWorkbook installWorkbook(SXSSFWorkbook workbook, String[] heads, String[] columns, Map<String, Object> statisticsMap, String excelName, List<T> result, int sheetNum)
            throws Exception {
        List<String> headList = new ArrayList<String>();
        List<String> columnsList = new ArrayList<String>();
        Collections.addAll(headList,heads);
        Collections.addAll(columnsList,columns);
        return installWorkbook(workbook,headList,columnsList,statisticsMap,excelName,result,sheetNum);
    }

    public SXSSFWorkbook installWorkbook(SXSSFWorkbook workbook, List<String> headList,List<String> columnsList, Map<String, Object> statisticsMap, String excelName, List<T> result, int sheetNum)
            throws Exception {
        return installWorkbook(workbook,headList,columnsList,statisticsMap,excelName,result,sheetNum,pattern);
    }

    /**
     * 说明：支持海量数据下载
     * @param workbook 工作薄
     * @param headList sheet表头
     * @param columnsList 表头对应的对象属性
     * @param statisticsMap 统计信息
     * @param excelName excel名字
     * @param result 结果集
     * @param sheetNum sheet编号
     * @param datePattern 日期类型格式   默认 : yyyy-MM-dd HH:mm:ss
     * @return 工作薄
     * @throws Exception
     */
    public SXSSFWorkbook installWorkbook(SXSSFWorkbook workbook, List<String> headList,List<String> columnsList, Map<String, Object> statisticsMap, String excelName, List<T> result, int sheetNum,String datePattern)
            throws Exception {
        logger.info(excelName + "下载中，请稍后…………" + sheetNum);
        Sheet sheet;
        if ((sheetNum - 1) % 5 == 0) { //每5万一个sheet
            sheet = workbook.createSheet(excelName + "-" + (sheetNum - 1) / 5);
            adjustColumnSize(sheet, headList);//自动列宽
            //1、统计信息
//            createCountRow(sheet, statisticsMap);
            //2、设置表头
            Row row = sheet.createRow(0);
            for (int a = 0; a < headList.size(); a++) {
                Cell cell = row.createCell(a);
                cell.setCellValue(headList.get(a));
                cell.setCellStyle(headStyle);
            }
        } else {
            sheet = workbook.getSheet(excelName + "-" + (sheetNum - 1) / 5);
        }

        //2、添加数据
        int rowStart = sheet.getLastRowNum();
        for (int b = 0; b < (result.size()); b++) {
            Row dateRow = sheet.createRow(rowStart + b + 1);
            T t = result.get(b);
            for (int c = 0; c < headList.size(); c++) {
                Cell cell = dateRow.createCell(c);
                String fieldName = columnsList.get(c);
                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Class tCls = t.getClass();
                Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                Object value = getMethod.invoke(t, new Class[]{});
                String textValue = null;
                if (value == null) {
                    textValue = "";
                } else if (value instanceof Date) {
                    Date date = (Date) value;
                    SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                    textValue = sdf.format(date);
                } else {
                    // 其它数据类型都当作字符串简单处理
                    textValue = value.toString();
                }

                if (textValue != null) {
                    Pattern p = Pattern.compile("^[+-]?(0|([1-9]\\d*))(\\.\\d+)?$");
                    Matcher matcher = p.matcher(textValue);
                    if (matcher.matches()) {
                        if(fieldName.toLowerCase().contains("rate")) {
                            CellStyle cellStyle = workbook.createCellStyle();
                            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
                            cell.setCellStyle(cellStyle);
                        }
                        // 是数字当作double处理
                        cell.setCellValue(Double.parseDouble(textValue));
                    } else {
                        HSSFRichTextString richString = new HSSFRichTextString(
                                textValue);
                        // HSSFFont font3 = workbook.createFont();
                        // font3.setColor(HSSFColor.BLUE.index);
                        // richString.applyFont(font3);
                        cell.setCellValue(richString);
                    }
                }
            }
        }
        return workbook;
    }

    /**
     * 说明：支持海量数据下载
     * @param workbook 工作薄
     * @param headList sheet表头
     * @param columnsList 表头对应的对象属性
     * @param excelName excel名字
     * @param array jsonArray结果集
     * @param sheetNum sheet编号
     * @param datePattern 日期类型格式   默认 : yyyy-MM-dd HH:mm:ss
     * @return 工作薄
     * @throws Exception
     */
    public SXSSFWorkbook installWorkbookByJsonArray(SXSSFWorkbook workbook, List<String> headList, List<String> columnsList, String excelName, JSONArray array, int sheetNum, String datePattern)
            throws Exception {
        logger.info(excelName + "下载中，请稍后………………" + sheetNum);
        Sheet sheet;
//        if ((sheetNum - 1) % 5 == 0) { //每5万一个sheet
            sheet = workbook.createSheet(excelName);
            adjustColumnSize(sheet, headList);//自动列宽
            //2、设置表头
            Row row = sheet.createRow(0);
            for (int a = 0; a < headList.size(); a++) {
                Cell cell = row.createCell(a);
                cell.setCellValue(headList.get(a));
                cell.setCellStyle(headStyle);
            }
//        } else {
//            sheet = workbook.getSheet(excelName + "-" + (sheetNum - 1) / 5);
//        }

        //2、添加数据
        int rowStart = sheet.getLastRowNum();
        for (int b = 0; b < (array.size()); b++) {
            Row dateRow = sheet.createRow(rowStart + b + 1);
            JSONObject t = array.getJSONObject(b);
            for (int c = 0; c < headList.size(); c++) {
                Cell cell = dateRow.createCell(c);
                String fieldName = columnsList.get(c);
                Object value = t.get(fieldName);
                String textValue = null;
                if (value == null) {
                    textValue = "";
                } else if (value instanceof Date) {
                    Date date = (Date) value;
                    SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                    textValue = sdf.format(date);
                } else {
                    // 其它数据类型都当作字符串简单处理
                    textValue = value.toString();
                }

                if (textValue != null) {
                    Pattern p = Pattern.compile("^[+-]?(0|([1-9]\\d*))(\\.\\d+)?$");
                    Matcher matcher = p.matcher(textValue);
                    if (matcher.matches()) {
                        if(fieldName.toLowerCase().contains("rate")) {
                            CellStyle cellStyle = workbook.createCellStyle();
                            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
                            cell.setCellStyle(cellStyle);
                        }
                        // 是数字当作double处理
                        cell.setCellValue(Double.parseDouble(textValue));
                    } else {
                        HSSFRichTextString richString = new HSSFRichTextString(
                                textValue);
                        // HSSFFont font3 = workbook.createFont();
                        // font3.setColor(HSSFColor.BLUE.index);
                        // richString.applyFont(font3);
                        cell.setCellValue(richString);
                    }
                }
            }
        }
        return workbook;
    }

    /**
     * 导出excel
     * @param request
     * @param response
     * @param wb
     * @param excelName 文件名
     * @throws Exception
     */
    public void export(HttpServletRequest request, HttpServletResponse response, SXSSFWorkbook wb, String excelName)throws Exception {
        logger.info("ExcelUtil<T>导出excel开始========>文件名："+excelName);
        ServletOutputStream out = null;
        try {
            response.setHeader("Cache-Control", "private");
            response.setHeader("Pragma", "private");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Type", "application/force-download");
            String name = processFileName(request, excelName + ".xlsx");
            response.setHeader("Content-disposition", "attachment;filename=" + name);
            out = response.getOutputStream();
            wb.write(out);
            out.flush();
            out.close();
        } catch (final IOException e) {
            throw e;
        }
        System.gc();
        logger.info("导出excel结束");
    }

    /**
     *
     * @param out 输入流
     * @param wb 输出的工作簿对象
     * @throws Exception
     */
    public void export(OutputStream out, SXSSFWorkbook wb)throws Exception {
            wb.write(out);
            out.flush();
            out.close();
    }


}
```

- 使用工具类实现海量数据下载（泛型）
```java
        //。。。。。。。
    int count= studentService.queryCount(studentQuery);;
        int size=0;
        int pageSize = 10000;  //每次查询数量
        ExcelUtil<Student>  excelUtil= new ExcelUtil<Student>();
        SXSSFWorkbook workbook = excelUtil.init();
        Map<String,Object> map=new HashMap<String, Object>()；

        Page page=new Page();
        size=count/pageSize +1;
        for(int i=1;i<size+1;i++){
            page.setCurrentPage(i);
            page.setPageSize(pageSize );
            ResultList result = studentService.queryStudent(studentQuery,page);
            workbook=excelUtil.installWorkbook(workbook,headList,map,excelName,result.getList(),i);
        }
        ExcelExport.export(request, response, workbook, excelName);
        //。。。。。。。。
```

- 使用工具类实现海量数据下载（jsonarry）
```java

    /**
     * 行业申请增长率趋势 -- 表格
     * @param dmrReqDvo
     * @return
     */
    public JSONObject qryFormByIndustryAppGrowthThred(final DmrReqDvo dmrReqDvo) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        JSONObject resultObj = new JSONObject();
        DmrReqDvo dd = (DmrReqDvo)BeanUtils.cloneBean(dmrReqDvo);
        List<Map<Object, Object>> maps = this.industryAppGrowthThred(dd);
        //表头
        ArrayList<String> header = new ArrayList<String>() {{
            add("客群");
            add("统计时间");
            add("申请量");
            if(StatisticalMethodConstant.FIXED_BASE.equals(dmrReqDvo.getStatisMethod())){
                add("定基比增长率");
            }else if(StatisticalMethodConstant.MOM.equals(dmrReqDvo.getStatisMethod())) {
                add("环比增长率");
            }
        }};
        //jsonField
        List<String> keyList = new ArrayList<>();
        keyList.add("comp_type");
        keyList.add("statis_month");
        if (StatisticalDimension.CNT.equals(dmrReqDvo.getStatisDim())) {
            keyList.add("req_cnt");
            keyList.add("req_cnt_rate");
        }else if(StatisticalDimension.USER.equals(dmrReqDvo.getStatisDim())){
            keyList.add("req_user");
            keyList.add("req_user_rate");
        }
        //data
        JSONArray array = new JSONArray();
        for (Map map: maps) {
            JSONObject item = new JSONObject();
            for (Object key : map.keySet()) {
                String keyStr = (String)key;
                if(keyList.contains(keyStr)){
                    item.put(keyStr, map.get(key));
                }
            }
            array.add(item);
        }
        //array转List 排序
        List<JSONObject> list = JSONArray.parseArray(array.toJSONString(), JSONObject.class);
        Collections.sort(list, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                String compType1 = o1.getString("comp_type");
                String compType2 = o2.getString("comp_type");
                if(compType1.compareTo(compType2)>0){
                    return 1;
                }else if(compType1.compareTo(compType2)< 0){
                    return -1;
                }else{
                    return 0;
                }
            }
        });

        JSONObject jsonObject = this.qryParameters();
        JSONObject busType = jsonObject.getJSONObject("busType");
        Map<String, String> busyTypeMap = new HashedMap();
        busyTypeMap = JSON.parseObject(busType.toJSONString(), Map.class);
        for (JSONObject item : list) {
            item.put("comp_type", busyTypeMap.get(item.get("comp_type")));
        }

        array = JSONArray.parseArray(list.toString());

        resultObj.put("headers", header);
        resultObj.put("keys", keyList);
        resultObj.put("arrays", array);
        return resultObj;
    }
    /**
     * 行业申请增长率趋势
     * @param dmrReqDvo
     * @param excelUtil
     * @param workbook
     * @return
     * @throws Exception
     */
    private SXSSFWorkbook createIndustryAppGrowthThredData(final DmrReqDvo dmrReqDvo, ExcelUtils<Object> excelUtil, SXSSFWorkbook workbook, int sheetNum) throws Exception {
        JSONObject object = this.qryFormByIndustryAppGrowthThred(dmrReqDvo);
        List headers = object.getObject("headers", List.class);
        List keys = object.getObject("keys", List.class);
        JSONArray array = object.getObject("arrays", JSONArray.class);
        workbook=excelUtil.installWorkbookByJsonArray(workbook,headers,keys,"行业申请增长率趋势",array,sheetNum,"yyyy-MM-dd");
        return workbook;
    }

    // 测试 (已验证 OK)
    public void ttt(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 表头
        ArrayList<String> header = new ArrayList<String>() {{
            add("客群");
            add("统计时间");
            add("申请量");
        }};

        // jsonField
        List<String> keyList = new ArrayList<String>() {{
            add("comp_type");
            add("static_month");
            add("req_cnt");
        }};

        // data
        JSONArray array = new JSONArray();
        JSONObject o1 = new JSONObject();
        o1.put("comp_type", "1a");
        o1.put("static_month", "1b");
        o1.put("req_cnt", "1c");
        JSONObject o2 = new JSONObject();
        o2.put("comp_type", "2a");
        o2.put("static_month", "2b");
        o2.put("req_cnt", "2c");
        array.add(o1);
        array.add(o2);
        for (int i = 3; i < 1000; i++) {
            JSONObject o = new JSONObject();
            o.put("comp_type", o + "a");
            o.put("static_month", o + "b");
            o.put("req_cnt", o + "c");
            array.add(o);
        }

        ExcelUtils<Object> excelUtils = new ExcelUtils();
        // 初始化
        SXSSFWorkbook book = excelUtils.init();
        // 生成 book
        excelUtils.installWorkbookByJsonArray(book, header, keyList, "行业增长率", array, 1, "yyyy-MM-dd");
        // response 输出
        excelUtils.export(request, response, book, "行业增长率");
    }

```
