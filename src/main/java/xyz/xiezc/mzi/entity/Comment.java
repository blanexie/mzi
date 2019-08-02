package xyz.xiezc.mzi.entity;

import xyz.xiezc.mzi.common.annotation.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * t_comment
 * @author 
 */
@Table("t_comment")
public class Comment implements Serializable {
    private Integer id;

    private Integer userId;

    /**
     * 评论楼层
     */
    private Integer index;

    /**
     * 两百字以内
     */
    private String content;

    /**
     * 关联回复楼层
     */
    private Integer toIndex;

    /**
     * 评论的图片
     */
    private Integer photoId;

    /**
     * 评论赞成数
     */
    private Integer agreeNum;

    private LocalDateTime createTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getToIndex() {
        return toIndex;
    }

    public void setToIndex(Integer toIndex) {
        this.toIndex = toIndex;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public Integer getAgreeNum() {
        return agreeNum;
    }

    public void setAgreeNum(Integer agreeNum) {
        this.agreeNum = agreeNum;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Comment other = (Comment) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getIndex() == null ? other.getIndex() == null : this.getIndex().equals(other.getIndex()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getToIndex() == null ? other.getToIndex() == null : this.getToIndex().equals(other.getToIndex()))
            && (this.getPhotoId() == null ? other.getPhotoId() == null : this.getPhotoId().equals(other.getPhotoId()))
            && (this.getAgreeNum() == null ? other.getAgreeNum() == null : this.getAgreeNum().equals(other.getAgreeNum()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getIndex() == null) ? 0 : getIndex().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getToIndex() == null) ? 0 : getToIndex().hashCode());
        result = prime * result + ((getPhotoId() == null) ? 0 : getPhotoId().hashCode());
        result = prime * result + ((getAgreeNum() == null) ? 0 : getAgreeNum().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", index=").append(index);
        sb.append(", content=").append(content);
        sb.append(", toIndex=").append(toIndex);
        sb.append(", photoId=").append(photoId);
        sb.append(", agreeNum=").append(agreeNum);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}