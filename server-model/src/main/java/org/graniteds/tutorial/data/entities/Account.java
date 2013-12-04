package org.graniteds.tutorial.data.entities;

import org.granite.messaging.annotations.Include;
import org.granite.tide.data.DataPublishListener;
import org.granite.util.UUIDUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Set;

// tag::entity[]
@Entity
@EntityListeners({ DataPublishListener.class }) // <1>
public class Account implements Serializable { // <2>

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Version
    private Long version; // <3>

    @Basic
    private String uid; // <4>

    @Basic
    @NotNull @Size(min=3, max=50)
    private String name; // <5>

    @Basic
    @NotNull @Size(min=3, max=50) @Pattern(regexp="^.*@.*$")
    private String email; // <5>

    @Basic
    private String webSite;

    @ElementCollection
    private Set<String> phones;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public Set<String> getPhones() {
        return phones;
    }

    public void setPhones(Set<String> phones) {
        this.phones = phones;
    }

    @Include
    public String getGravatarUrl() { // <6>
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(email.trim().toLowerCase().getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest)
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1,3));
            return sb.toString();
        }
        catch (Exception e) {
            return null;
        }
    }

    @PrePersist
    private void initUid() {
        if (uid == null)
            uid = UUIDUtil.randomUUID();
    }
}
// end::entity[]