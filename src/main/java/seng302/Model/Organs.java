package seng302.Model;



public enum Organs {
  LIVER("Liver") ,
  KIDNEY("Kidney"),
  PANCREAS("Pancreas"),
  HEART("Heart"),
  LUNG("Lung"),
  INTESTINE("Intestine"),
  CORNEA("Cornea"),
  MIDDLE_EAR("Middle Ear"),
  SKIN("Skin"),
  BONE_MARROW("Bone Marrow"),
  BONE("Bone"),
  CONNECTIVE_TISSUE("Connective Tissue");

  public String organName;

  Organs(String organName){
    this.organName = organName;
  }

  @Override
  public String toString(){
    return organName;
  }


}
