# 38译码器

```verilog
`timescale 1ns / 1ps

module decode_38(
    input [2:0] in,
    output reg [7:0] out
    );
    
    always@(in)begin
        case(in)
            3'b000: out = 8'b00000001;
            3'b001: out = 8'b00000010;
            3'b010: out = 8'b00000100;
            3'b011: out = 8'b00001000;
            3'b100: out = 8'b00010000;
            3'b101: out = 8'b00100000;
            3'b110: out = 8'b01000000;
            3'b111: out = 8'b10000000;
        endcase
    end
endmodule
```

```verilog
`timescale 1ns / 1ps

module decode_38_tb();
    reg [2:0] in_tb;
    wire [7:0] decode;
    decode_38 test(
        .in(in_tb),
        .out(decode)
    );
    initial begin
        in_tb = 3'b000;
        #10
        in_tb = 3'b001;
        #10
        in_tb = 3'b010;
        #10
        in_tb = 3'b011;
        #10
        in_tb = 3'b100;
        #10
        in_tb = 3'b101;
        #10
        in_tb = 3'b110;
        #10
        in_tb = 3'b111;
        #10
        $stop;
    end
    
endmodule

```

# 83编码器

```verilog
`timescale 1ns / 1ps


module encode83(
    input [7:0] in,
    output reg [2:0] out
    );
always @(*)begin
    assign out = 
        in[0]? 3'b000 : 
        in[1]? 3'b001 :
        in[2]? 3'b010 :
        in[3]? 3'b011 :
        in[4]? 3'b100 :
        in[5]? 3'b101 :
        in[6]? 3'b110 :
        3'b111;
end
endmodule

```

```verilog
`timescale 1ns / 1ps


module encode83_tb();
    reg [7:0] in_tb;
    wire [2:0] out_tb;
    encode83 encode_tb(.in(in_tb), .out(out_tb));
    integer i = 0;
    initial begin
        for(i = 0; i < 8; i = i + 1)begin
            # 10
            if(i != 0)begin
                in_tb[i-1] = 0;
            end
            in_tb[i] = 1;
        end
        $stop;
    end
endmodule

```

# 5选1多线路选择器

```verilog
`timescale 1ns / 1ps


module select51(
    input [7:0] in0,
    input [7:0] in1,
    input [7:0] in2,
    input [7:0] in3,
    input [7:0] in4,
    input [2:0] seed,
    output reg [7:0] out
    );
always @(*)begin
    case(seed)
        3'b000: out = in0;
        3'b001: out = in1;
        3'b010: out = in2;
        3'b011: out = in3;
        3'b100: out = in4;
        default: out = 3'b000;
    endcase
end
endmodule

```

```verilog
`timescale 1ns / 1ps


module select51_tb();
reg [7:0] in0_tb;
reg [7:0] in1_tb;
reg [7:0] in2_tb;
reg [7:0] in3_tb;
reg [7:0] in4_tb;
reg [2:0] seed_tb;
wire [7:0] out_tb;
select51 select51_tb(in0_tb, in1_tb, in2_tb, in3_tb, in4_tb, seed_tb, out_tb);
initial begin
    in0_tb = 1; in1_tb = 0; in2_tb = 0; in3_tb = 0; in4_tb = 0;
    #10
    seed_tb = 3'b000;
    #10
    seed_tb = 3'b001;
    in0_tb = 0;
    in1_tb = 1;
    #10
    seed_tb = 3'b010;
    in1_tb = 0;
    in2_tb = 1;
    #10
    in2_tb = 0;
    in3_tb = 1;
    seed_tb = 3'b011;
    #10
    seed_tb = 3'b100;
    in3_tb = 0;
    in4_tb = 1;
    $finish;
end
endmodule

```

