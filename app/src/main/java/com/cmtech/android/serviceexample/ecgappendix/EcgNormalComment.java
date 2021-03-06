package com.cmtech.android.serviceexample.ecgappendix;

import com.cmtech.android.serviceexample.bmefile.ByteUtil;
import com.cmtech.android.serviceexample.bmefile.DataIOUtil;
import com.cmtech.android.serviceexample.ecgfile.DateTimeUtil;
import com.cmtech.android.serviceexample.ecgfile.User;
import com.cmtech.android.serviceexample.ecgfile.UserManager;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

/**
 * EcgNormalComment: 心电一般留言类
 * Created by bme on 2019/1/9.
 */

public class EcgNormalComment extends EcgAppendix{
    private static final int CONTENT_CHAR_NUM = 500; // 内容字符数
    private static final int MODIFY_TIME_BYTE_NUM = 8;

    private User creator = new User(); // 创建人
    private long modifyTime = -1; // 修改时间
    private String content = ""; // 内容

    private EcgNormalComment() {
    }

    private EcgNormalComment(User creator, long modifyTime) {
        this();
        try {
            this.creator = (User) creator.clone();
            this.modifyTime = modifyTime;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException();
        }
    }

    /**
     * 用当前账户和当前时间创建默认留言
     * @return 默认留言对象
     */
    public static EcgNormalComment createDefaultComment() {
        User creator = UserManager.getInstance().getUser();
        long modifyTime = new Date().getTime();
        return new EcgNormalComment(creator, modifyTime);
    }

    public static EcgNormalComment create() {
        return new EcgNormalComment();
    }

    public User getCreator() {
        return creator;
    }
    public long getModifyTime() {
        return modifyTime;
    }
    public void setModifyTime(long modifyTime) { this.modifyTime = modifyTime;}
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 添加留言内容
      */
    public void appendContent(String content) {
        this.content += content;
    }

    @Override
    public EcgAppendixType getType() {
        return EcgAppendixType.NORMAL_COMMENT;
    }

    /**
     * 从数据输入流读取
     * @param in：数据输入流
     */
    @Override
    public void readFromStream(DataInput in) throws IOException{
        creator.readFromStream(in); // 读创建人
        modifyTime = ByteUtil.reverseLong(in.readLong()); // 读修改时间
        content = DataIOUtil.readFixedString(in, CONTENT_CHAR_NUM); // 读留言内容
    }

    /**
     * 写出到数据输出流
     * @param out：数据输出流
     */
    @Override
    public void writeToStream(DataOutput out) throws IOException{
        creator.writeToStream(out); // 写创建人
        out.writeLong(ByteUtil.reverseLong(modifyTime)); // 写修改时间
        DataIOUtil.writeFixedString(out, content, CONTENT_CHAR_NUM); // 写留言内容
    }

    /**
     * 获取留言的字节长度
     * @return 字节长
     */
    @Override
    public int length() {
        return  super.length() + creator.length() + MODIFY_TIME_BYTE_NUM + 2* CONTENT_CHAR_NUM;
    }

    @Override
    public String toString() {
        return creator.getName() + "@" + DateTimeUtil.timeToShortStringWithTodayYesterday(modifyTime) + ' ' + content;
    }

    @Override
    public boolean equals(Object otherObject) {
        if(this == otherObject) return true;
        if(otherObject == null) return false;
        if(getClass() != otherObject.getClass()) return false;

        EcgNormalComment other = (EcgNormalComment)otherObject;
        // 只要手机号和修改时间相同，就认为是同一条留言
        return  (creator.getPhone().equals(other.creator.getPhone()) && (modifyTime == other.modifyTime));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37*result + creator.hashCode();
        result = 37*result + (int)(modifyTime^(modifyTime>>32));
        return result;
    }
}
