package abct.Utils;

public class DuplicateFileReturn {
    private String fileName;
    private Boolean isCanceled;


    public DuplicateFileReturn(){
        this.fileName = "error";
        this.isCanceled = true;
    }

    public DuplicateFileReturn(String fileName, Boolean isCanceled) {
     this.fileName = fileName;
     this.isCanceled = isCanceled;
    }

    public String getFileName() {
        return fileName;
    }

    public Boolean getIsCanceled() {
        return isCanceled;
    }
}
