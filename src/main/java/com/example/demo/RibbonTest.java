package com.example.demo;

import com.google.common.collect.Lists;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import com.netflix.loadbalancer.reactive.ServerOperation;
import rx.Observable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @Classname RibbonTest
 * @Description TODO
 * @Date 2020/5/30 2:02
 * @Created by gumei
 * @Author: lepua
 */
public class RibbonTest {
    public static void main(String[] args) {
        List<Server> serverList= Lists.newArrayList(new Server("localhost",8081));
        ILoadBalancer loadBalancer= LoadBalancerBuilder.newBuilder().buildFixedServerListLoadBalancer(serverList);
        for(int i=0;i<5;i++){
            String result= LoadBalancerCommand.<String>builder().withLoadBalancer(loadBalancer).build().submit(
                    new ServerOperation<String>() {
                        @Override
                        public Observable<String> call(Server server) {
                            String addr="http://"+server.getHost()+":"+server.getPort()+"/user/hello";
                            System.out.println("调用地址："+addr);
                            try {
                                URL url=new URL(addr);
                                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");
                                connection.connect();
                                InputStream inputStream=connection.getInputStream();
                                byte[] data=new byte[inputStream.available()];
                                inputStream.read(data);
                                return Observable.just(new String(data));


                            } catch (Exception e) {
                                return Observable.error(e);
                            }


                        }
                    }
            ).toBlocking().first();

            System.out.println("调用结果："+result);
        }
    }


}
