public class City implements Comparable<City> {
    private final String name;
    private final int x, y;

    public City(String name, int x, int y) {
        if (name == null) throw new IllegalArgumentException("name cannot be null");
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public String toString() {
        return String.format("%s (%d, %d)", name, x, y);
    }

    @Override
    public int compareTo(City other) {
        int c = name.compareTo(other.name);
        return c;
    }
    
    public boolean equals(String otherName) {
        return this.name.equals(otherName);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;
        City other = (City) o;
        return name.equals(other.name) && x == other.x && y == other.y;
    }
}
