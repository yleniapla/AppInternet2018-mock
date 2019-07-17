package it.polito.ai.server.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

@Document
public class User {

    @Id
    private String username;
    private String password;
    @DBRef
    private LinkedList<Archive> archives = new LinkedList<>();
    @DBRef
    private LinkedList<Archive> boughtArchives = new LinkedList<>();

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public String getUsername() { return username; }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role){
        if (this.roles == null)
            this.roles = new HashSet<>();
        this.roles.add(role);
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public LinkedList<Archive> getArchives() {
        return archives;
    }

    public void setArchive(LinkedList<Archive> a) {
        this.archives = a;
    }

    public void addArchive(LinkedList<Archive> a) {
        if (this.archives.isEmpty()){
            this.archives = new LinkedList<>();
        }
        this.archives.addAll(a);
    }

    public void appendArchive(Archive a) { this.archives.add(a);}

    public void removeArchive(String id) {
        for(Iterator<Archive> iter = this.archives.iterator(); iter.hasNext();)
            if(iter.next().getId().equals(new ObjectId(id))) {
                iter.remove();
                break;
            }
    }

    public Archive getLastArchive() { return this.archives.getLast();}

    public LinkedList<Archive> getBoughtArchives() {
        return boughtArchives;
    }

    public void setBoughtArchive(LinkedList<Archive> a) {
        this.boughtArchives = a;
    }

    public void addBoughtArchive(LinkedList<Archive> a) {
        if (this.boughtArchives.isEmpty()){
            this.boughtArchives= new LinkedList<>();
        }
        this.boughtArchives.addAll(a);
    }

    public void appendBoughtArchive(Archive a) { this.boughtArchives.add(a);}

    public Archive getLastBoughtArchive() { return this.boughtArchives.getLast();}

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", archives=" + archives +
                ", roles=" + roles +
                '}';
    }
}
