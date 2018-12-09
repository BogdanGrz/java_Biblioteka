package model;

public class Stan {
    private int id_stanu;
    private String Stan;

    public int getId_stanu() {
        return id_stanu;
    }

    public void setId_stanu(int id_stanu) {
        this.id_stanu = id_stanu;
    }

    public String getStan() {
        return Stan;
    }

    public void setStan(String Stan) {
        this.Stan = Stan;
    }

    public Stan(int id_stanu, String Stan) {
        this.id_stanu = id_stanu;
        this.Stan = Stan;
    }

   
   
    @Override
    public String toString() {
        return "["+id_stanu+"] - "+Stan+"\n";
    }
}