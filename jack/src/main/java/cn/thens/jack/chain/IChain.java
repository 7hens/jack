package cn.thens.jack.chain;

/**
 * @author 7hens
 */
public interface IChain<T> {
    Chain<T> asChain() throws Throwable;
}
