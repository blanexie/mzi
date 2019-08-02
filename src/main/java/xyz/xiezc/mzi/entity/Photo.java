package xyz.xiezc.mzi.entity;

import xyz.xiezc.mzi.common.annotation.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * t_photo
 * @author 
 */
@Table("t_photo")
public class Photo implements Serializable {
    private Integer id;

    private String fetchUrl;

    private String path;

    private Integer size;

    private String showTime;

    private Integer albumId;

    private LocalDateTime createTime;

    private Integer see;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFetchUrl() {
        return fetchUrl;
    }

    public void setFetchUrl(String fetchUrl) {
        this.fetchUrl = fetchUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getSee() {
        return see;
    }

    public void setSee(Integer see) {
        this.see = see;
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
        Photo other = (Photo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getFetchUrl() == null ? other.getFetchUrl() == null : this.getFetchUrl().equals(other.getFetchUrl()))
            && (this.getPath() == null ? other.getPath() == null : this.getPath().equals(other.getPath()))
            && (this.getSize() == null ? other.getSize() == null : this.getSize().equals(other.getSize()))
            && (this.getShowTime() == null ? other.getShowTime() == null : this.getShowTime().equals(other.getShowTime()))
            && (this.getAlbumId() == null ? other.getAlbumId() == null : this.getAlbumId().equals(other.getAlbumId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getSee() == null ? other.getSee() == null : this.getSee().equals(other.getSee()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getFetchUrl() == null) ? 0 : getFetchUrl().hashCode());
        result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
        result = prime * result + ((getSize() == null) ? 0 : getSize().hashCode());
        result = prime * result + ((getShowTime() == null) ? 0 : getShowTime().hashCode());
        result = prime * result + ((getAlbumId() == null) ? 0 : getAlbumId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getSee() == null) ? 0 : getSee().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", fetchUrl=").append(fetchUrl);
        sb.append(", path=").append(path);
        sb.append(", size=").append(size);
        sb.append(", showTime=").append(showTime);
        sb.append(", albumId=").append(albumId);
        sb.append(", createTime=").append(createTime);
        sb.append(", see=").append(see);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}