package be.johanaerens.domain;

import be.johanaerens.domain.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Asset.
 */
@Entity
@Table(name = "asset")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Asset implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "number")
    private String number;

    @Column(name = "brand")
    private String brand;

    @Column(name = "model")
    private String model;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "purchase_date")
    private Instant purchaseDate;

    @Column(name = "warant_date")
    private Instant warantDate;

    @Column(name = "comments")
    private String comments;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assets", "assetHistory" }, allowSetters = true)
    private Employee employee;

    @JsonIgnoreProperties(value = { "asset", "employee" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "asset")
    private AssetHistory assetHistory;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Asset id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return this.number;
    }

    public Asset number(String number) {
        this.setNumber(number);
        return this;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBrand() {
        return this.brand;
    }

    public Asset brand(String brand) {
        this.setBrand(brand);
        return this;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return this.model;
    }

    public Asset model(String model) {
        this.setModel(model);
        return this;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public Asset serialNumber(String serialNumber) {
        this.setSerialNumber(serialNumber);
        return this;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Instant getPurchaseDate() {
        return this.purchaseDate;
    }

    public Asset purchaseDate(Instant purchaseDate) {
        this.setPurchaseDate(purchaseDate);
        return this;
    }

    public void setPurchaseDate(Instant purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Instant getWarantDate() {
        return this.warantDate;
    }

    public Asset warantDate(Instant warantDate) {
        this.setWarantDate(warantDate);
        return this;
    }

    public void setWarantDate(Instant warantDate) {
        this.warantDate = warantDate;
    }

    public String getComments() {
        return this.comments;
    }

    public Asset comments(String comments) {
        this.setComments(comments);
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Status getStatus() {
        return this.status;
    }

    public Asset status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Employee getEmployee() {
        return this.employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Asset employee(Employee employee) {
        this.setEmployee(employee);
        return this;
    }

    public AssetHistory getAssetHistory() {
        return this.assetHistory;
    }

    public void setAssetHistory(AssetHistory assetHistory) {
        if (this.assetHistory != null) {
            this.assetHistory.setAsset(null);
        }
        if (assetHistory != null) {
            assetHistory.setAsset(this);
        }
        this.assetHistory = assetHistory;
    }

    public Asset assetHistory(AssetHistory assetHistory) {
        this.setAssetHistory(assetHistory);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Asset)) {
            return false;
        }
        return getId() != null && getId().equals(((Asset) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Asset{" +
            "id=" + getId() +
            ", number='" + getNumber() + "'" +
            ", brand='" + getBrand() + "'" +
            ", model='" + getModel() + "'" +
            ", serialNumber='" + getSerialNumber() + "'" +
            ", purchaseDate='" + getPurchaseDate() + "'" +
            ", warantDate='" + getWarantDate() + "'" +
            ", comments='" + getComments() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
