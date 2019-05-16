package com.shizhenbao.pop;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fly on 2017/3/23.
 */
//测试账号： a 密码123
public class  User extends LitePalSupport implements Serializable{
    private String pId;//病人的id
    private String pName;//病人的姓名
    private String tel;//病人的电话号码
    private String age;//病人的年龄
    private String checkDate;//病人的pdf生成日期

    private int operId;//医生的id ，表示这个病人是哪个医生看的病
    private String symptom;//症状
    private String summary;//总体评价
    private List<String> imgPath;//病变的区域的图片的地址（应该有三张图片）
    private String advice;//细胞学
    private boolean bResult;//是否已经出了报告
    private String Hpv_dna;//dna检测
    private String bingbian;//病变区域
    private String nizhen;//医生拟真
    private String yindaojin;//阴道镜所见
    private String zhuyishixiang;//注意事项
    private int is_diag;//是否添加诊断信息 1:未成报告 2：生成报告
    private String registDate;//病人的登记日期
    private long registDateint;//病人的登记日期 用于查询时 日期的比较

    private String bianjie;//边界/表面
    private String color;//颜色
    private String xueguna;//血管
    private String dianranse;//碘染色
    private String cusuan;//醋酸
    private String handle;//处理意见


    private String pSource;//病人来自哪里
    private String marry;//病人是否结婚
    private String hCG;
    private String work;//职业
    private String pregnantCount;//怀孕次数
    private String childCount;//产次
    private String abortionCount;//流产次数
    private String bloodType;//血型
    private String sexPartnerCount;//性伙伴数量
    private String smokeTime;//吸烟史
    private String birthControlMode;//避孕方式
    private String checkNotes;//检查注释
    private String idNumber;//身份证
    private String caseNumbe;//病例号
    private String sSNumber;//社保号码
    private int image;//判断该患者是否选择图片,1否，2是
    private String gatherPath;//采集图片路径

    private int surfacenum;//边界评分
    private int colornnum;//颜色评分
    private int vesselnum;//血管评分
    private int stainnum;//碘染色评分

    private String cutPath;

    private String pdfPath;//pdf保存路径

    public int getSurfacenum() {
        return surfacenum;
    }

    public void setSurfacenum(int surfacenum) {
        this.surfacenum = surfacenum;
    }

    public int getColornnum() {
        return colornnum;
    }

    public void setColornnum(int colornnum) {
        this.colornnum = colornnum;
    }

    public int getVesselnum() {
        return vesselnum;
    }

    public void setVesselnum(int vesselnum) {
        this.vesselnum = vesselnum;
    }

    public int getStainnum() {
        return stainnum;
    }

    public void setStainnum(int stainnum) {
        this.stainnum = stainnum;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public long getRegistDateint() {
        return registDateint;
    }

    public void setRegistDateint(long registDateint) {
        this.registDateint = registDateint;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public String getCutPath() {
        return cutPath;
    }

    public void setCutPath(String cutPath) {
        this.cutPath = cutPath;
    }

    public String getGatherPath() {
        return gatherPath;
    }

    public void setGatherPath(String gatherPath) {
        this.gatherPath = gatherPath;
    }

    public String gethCG() {
        return hCG;
    }

    public void sethCG(String hCG) {
        this.hCG = hCG;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getPregnantCount() {
        return pregnantCount;
    }

    public void setPregnantCount(String pregnantCount) {
        this.pregnantCount = pregnantCount;
    }

    public String getChildCount() {
        return childCount;
    }

    public void setChildCount(String childCount) {
        this.childCount = childCount;
    }

    public String getAbortionCount() {
        return abortionCount;
    }

    public void setAbortionCount(String abortionCount) {
        this.abortionCount = abortionCount;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getSexPartnerCount() {
        return sexPartnerCount;
    }

    public void setSexPartnerCount(String sexPartnerCount) {
        this.sexPartnerCount = sexPartnerCount;
    }

    public String getSmokeTime() {
        return smokeTime;
    }

    public void setSmokeTime(String smokeTime) {
        this.smokeTime = smokeTime;
    }

    public String getBirthControlMode() {
        return birthControlMode;
    }

    public void setBirthControlMode(String birthControlMode) {
        this.birthControlMode = birthControlMode;
    }

    public String getCheckNotes() {
        return checkNotes;
    }

    public void setCheckNotes(String checkNotes) {
        this.checkNotes = checkNotes;
    }

    public String getBianjie() {
        return bianjie;
    }

    public void setBianjie(String bianjie) {
        this.bianjie = bianjie;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getXueguna() {
        return xueguna;
    }

    public void setXueguna(String xueguna) {
        this.xueguna = xueguna;
    }

    public String getDianranse() {
        return dianranse;
    }

    public void setDianranse(String dianranse) {
        this.dianranse = dianranse;
    }

    public String getCusuan() {
        return cusuan;
    }

    public void setCusuan(String cusuan) {
        this.cusuan = cusuan;
    }

    public int getIs_diag() {
        return is_diag;
    }

    public void setIs_diag(int is_diag) {
        this.is_diag = is_diag;
    }

    public String getZhuyishixiang() {
        return zhuyishixiang;
    }

    public void setZhuyishixiang(String zhuyishixiang) {
        this.zhuyishixiang = zhuyishixiang;
    }

    public String getHpv_dna() {
        return Hpv_dna;
    }

    public void setHpv_dna(String hpv_dna) {
        Hpv_dna = hpv_dna;
    }

    public String getBingbian() {
        return bingbian;
    }

    public void setBingbian(String bingbian) {
        this.bingbian = bingbian;
    }

    public String getNizhen() {
        return nizhen;
    }

    public void setNizhen(String nizhen) {
        this.nizhen = nizhen;
    }

    public String getYindaojin() {
        return yindaojin;
    }

    public void setYindaojin(String yindaojin) {
        this.yindaojin = yindaojin;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getpSource() {
        return pSource;
    }

    public void setpSource(String pSource) {
        this.pSource = pSource;
    }

    public String getMarry() {
        return marry;
    }

    public void setMarry(String marry) {
        this.marry = marry;
    }

    public int getOperId() {
        return operId;
    }

    public void setOperId(int operId) {
        this.operId = operId;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getImgPath() {
        return imgPath;
    }

    public void setImgPath(List<String> imgPath) {
        this.imgPath = imgPath;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public boolean isbResult() {
        return bResult;
    }

    public void setbResult(boolean bResult) {
        this.bResult = bResult;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getCaseNumbe() {
        return caseNumbe;
    }

    public void setCaseNumbe(String caseNumbe) {
        this.caseNumbe = caseNumbe;
    }

    public String getsSNumber() {
        return sSNumber;
    }

    public void setsSNumber(String sSNumber) {
        this.sSNumber = sSNumber;
    }


    public String getRegistDate() {
        return registDate;
    }

    public void setRegistDate(String registDate) {
        this.registDate = registDate;
    }

}
