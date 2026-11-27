# Las clases del modelo se guardan con serialización Java: sus nombres y campos
# deben mantenerse para poder leer los ficheros .dat de versiones anteriores.
-keep class es.huellas.model.** { *; }
-keep class es.huellas.net.** { *; }
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
