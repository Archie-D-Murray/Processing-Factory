package factory;

import java.util.stream.*;

public class Stats {
    public float speed;
	public float firePower;
	public float storage;
	public float weight;

    public Stats(float speed, float firePower, float storage, float weight) {
        this.speed = speed;
        this.firePower = firePower; 
        this.storage = storage;
        this.weight = weight;
    }

    public float compare(Stats other) {
        float[] stats = new float[] {
            other.speed / speed,
            other.firePower / firePower,
            other.storage / storage,
            weight / other.weight
        };
        return (float) IntStream.range(0, stats.length).mapToDouble(i -> stats[i]).sum() / stats.length;
    }

	public void add(Stats stats) {
		speed += stats.speed;
		firePower += stats.firePower;
		storage += stats.storage;
		weight += stats.weight;
	}

    @Override
    public String toString() {
        return String.format(
            "SPEED: %d\nFIREPOWER : %d\nSTORAGE: %d\nWEIGHT: %d", 
            Factory.round(speed),
            Factory.round(firePower),
            Factory.round(storage),
            Factory.round(weight)
        );
    }
}
