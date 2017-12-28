package dbk.generator.types;

import dbk.generator.probability.Carry;

public class Bone {
    final int ones;
    final boolean five;

    public Bone(int value) {
        if (value > 9 || value <0) {
            throw new IllegalArgumentException("it's impossible value for bone "+ value);
        }
        five = value > 5;
        ones = (five) ? value - 5 : value;
    }

    public int getValue() {
        return ((five)? 5: 0) + ones;
    }

    public Result add(Bone arg){
        int value = this.getValue() + arg.getValue();
        final Carry carry;
        final Bone bone;
        if (value > 9) {
            carry = Carry.PLUS;
            bone = new Bone(value - 10);
        } else {
            carry = Carry.NONE;
            bone = new Bone(value);
        }
        return new Result(bone, carry);
    }

    public Result minus(Bone arg){
        int value = this.getValue() - arg.getValue();
        final Carry carry;
        final Bone bone;
        if (value < 0) {
            carry = Carry.MINUS;
            bone = new Bone(value + 10);
        } else {
            carry = Carry.NONE;
            bone = new Bone(value);
        }
        return new Result(bone, carry);
    }

    class Result {
        final Bone bone;
        final Carry carry;

        Result(Bone bone, Carry carry) {
            this.bone = bone;
            this.carry = carry;
        }
    }
}
