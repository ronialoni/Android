package il.ac.tau.team3.shareaprayer;

public interface IStatusWriter {
	public void write(String s, long time);
	public void write(String s, int iconRes, long time);
}
