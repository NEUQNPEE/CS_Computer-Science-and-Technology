# 38译码器

## 设计

```verilog
`timescale 1ns / 1ps

module decoder_38(
    input wire [2:0]in,
    output wire [7:0]out
    );
    assign out[0]=(in==3'd0);
    assign out[1]=(in==3'd1);
    assign out[2]=(in==3'd2);
    assign out[3]=(in==3'd3);
    assign out[4]=(in==3'd4);
    assign out[5]=(in==3'd5);
    assign out[6]=(in==3'd6);
    assign out[7]=(in==3'd7);
    
endmodule
```

## 仿真

```verilog
`timescale 1ns / 1ps

module decoder_38_tb();
    reg [2:0] in0;
    wire [7:0] out0;
    decoder_38 dl(
        .in(in0),
        .out(out0)
        );
    integer i=0;
    initial begin
        for(i=0;i<=7;i=i+1)begin
            in0=i;
            #10;
        end
        $finish;
    end
  
endmodule
```

# 83编码器

## 设计

```verilog
`timescale 1ns / 1ps

module encoder_83(
    input wire[7:0] in,
    output wire[2:0] out
    );
    assign out=in[0]?3'd0:
                in[1]?3'd1:
                in[2]?3'd2:
                in[3]?3'd3:
                in[4]?3'd4:
                in[5]?3'd5:
                in[6]?3'd6:
                in[7]?3'd7:
                3'd0;
                
endmodule
```

## 仿真

```verilog
`timescale 1ns / 1ps

module encoder_83_tb();
    reg [7:0] in0;
    wire [2:0] out0;
    encoder_83 el(in0,out0);
    integer i;
    initial begin
        in0=1;
        #10;
        for(i=0;i<=6;i=i+1)begin
            in0=in0<<1;
            #10;
        end
        $stop;
    end

endmodule
```

# 5选1多线路选择器

## 设计
```verilog
`timescale 1ns / 1ps

module mux_51(
    input wire [2:0] sel,
    input wire [7:0] in0,in1,in2,in3,in4,
    output wire [7:0] out
    );
    assign out=(sel==3'd0)?in0:
                (sel==3'd1)?in1:
                (sel==3'd2)?in2:
                (sel==3'd3)?in3:
                (sel==3'd4)?in4:
                8'd0;

endmodule
```

## 仿真

```verilog
`timescale 1ns / 1ps

module mux_51_tb();
    reg [2:0] sel;
    reg [7:0] in0,in1,in2,in3,in4;
    wire [7:0] out;
    mux_51 m1(sel,in0,in1,in2,in3,in4,out);
    integer i=0;
    initial begin
        in0=8'h20;
        in1=8'h23;
        in2=8'h10;
        in3=8'h10;
        in4=8'h15;
        for(i=0;i<=4;i=i+1)begin
            sel=i;
            #10;
        end
        $finish;
    end

endmodule
```
