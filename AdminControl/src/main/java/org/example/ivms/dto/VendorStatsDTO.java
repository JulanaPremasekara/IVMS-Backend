package org.example.ivms.dto;

public class VendorStatsDTO {
    private Long total;
    private Long pending;
    private Long blacklisted;

    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }

    public Long getPending() { return pending; }
    public void setPending(Long pending) { this.pending = pending; }

    public Long getBlacklisted() { return blacklisted; }
    public void setBlacklisted(Long blacklisted) { this.blacklisted = blacklisted; }
}