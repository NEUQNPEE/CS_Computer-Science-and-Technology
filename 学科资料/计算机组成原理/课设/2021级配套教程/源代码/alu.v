`timescale 1ns / 1ns
`include "define.vh"
module alu(

    input wire rst,
    // 运算类型
    input[`AluOpBus] alu_control,
    // 源操作数 1
    input[31:0] alu_src1,
    // 源操作数 2
    input[31:0] alu_src2,
    // 要求写入的寄存器
    input wire[`RegAddrBus] wd_i,
    // 是否写入
    input wire wreg_i,
    // 运算结果
    output reg[31:0] alu_result,
    // 输出：写入寄存器地址
    output reg[`RegAddrBus] wd_o,
    // 输出：是否写入
    output reg wreg_o,
    
    // 指令
    input wire[`RegBus] inst_i,
    
    // 运算类型输出
    output wire[`AluOpBus] aluop_o,
    // 访存地址
    output wire[`RegBus] mem_addr_o,
    // 存储存到存储器的数
    output wire[`RegBus] reg2_o,
    
    input wire[`RegBus] link_address_i,
    // 暂时用不到？
    input wire is_in_delayslot_i,
    
    // 请求流水线中断
    output reg stallreq
    
    // // div
    // input wire[63:0] div_result_i,
    // input wire div_ready_i,
    
    // output reg[`RegBus] div_opdata1_o,
    // output reg[`RegBus] div_opdata2_o,
    // output reg div_start_o,
    // output reg signed_div_o,
    
    // // mul
    // input wire[63:0] mul_result_i,
    // input wire mul_ready_i,
    // output reg[`RegBus] mul_opdata1_o,
    // output reg[`RegBus] mul_opdata2_o,
    // output reg mul_start_o,
    // // 有无符号
    // output reg signed_mul_o,
    
    // //解决移动指令数据相关的相关接口
    // input wire[`RegBus] hi_i,
    // input wire[`RegBus] lo_i,
    // //回写时要写HI、LO否？
    // input wire[`RegBus] wb_hi_i,
    // input wire[`RegBus] wb_lo_i,
    // input wire wb_whilo_i,
    // //访存时要写HI、LO否？
    // input wire[`RegBus] mem_hi_i,
    // input wire[`RegBus] mem_lo_i,
    // input wire mem_whilo_i,
    // // HI LO传送给下一级
    // output reg[`RegBus] hi_o,
    // output reg[`RegBus] lo_o,
    // output reg whilo_o
    
    );
    
    // //因为除法和乘法暂停流水线标志
    // reg stallreq_for_div;
    // reg stallreq_for_mul;
    
    reg[`RegBus] logicout;  //逻辑操作结果
    reg[`RegBus] shiftres;  //移位操作结果
    reg[`RegBus] moveres;  //移动操作结果
    // //乘除法专用寄存器
    // reg[`RegBus] HI;
    // reg[`RegBus] LO;
    
    //  //最新的HI、LO值
    //    always @ (*) begin
    //        if(rst == `RstEnable) begin
    //            {HI,LO} <= {`ZeroWord,`ZeroWord};
    //            //如果访存时要写寄存器，那么直接把将写入值付给HI LO
    //        end else if(mem_whilo_i == `WriteEnable) begin
    //            {HI,LO} <= {mem_hi_i,mem_lo_i};
    //            // 如果回写时要写寄存器，那么直接把写入值赋给HI LO
    //        end else if(wb_whilo_i == `WriteEnable) begin
    //            {HI,LO} <= {wb_hi_i,wb_lo_i};
    //            //正常赋值
    //        end else begin
    //            {HI,LO} <= {hi_i,lo_i};            
    //        end
    //    end    
       
    //    // 移动相关 指令
    //    always @ (*) begin
    //            if(rst == `RstEnable) begin
    //              moveres <= `ZeroWord;
    //          end else begin
    //           moveres <= `ZeroWord;
    //           case (alu_control)
    //                 // MFHI 是 HI 入寄存器 rd
    //               `MFHI_OP:begin
    //                   moveres <= HI;
    //               end
    //               // MFLO 是 LO 入寄存器 rd
    //               `MFLO_OP: begin
    //                   moveres <= LO;
    //               end
    //               default : begin
    //               end
    //           endcase
    //          end
    //        end     
    
    //溢出确认
    wire ov_sum;
    
    assign aluop_o = alu_control;
    
    // 15:0这里是offset，src1是base，带符号拓展至32位，LW
    assign mem_addr_o = alu_src1 + {{16{inst_i[15]}},inst_i[15:0]};
    
    assign reg2_o = alu_src2;
    
    wire[31:0] alu_src2_mux;
    wire[31:0] result_sum;
    
    assign alu_src2_mux = 
    (alu_control==`SUB_OP||alu_control==`SUBU_OP||alu_control==`SLT_OP||alu_control==`SLTI_OP)
    ?(~alu_src2)+1:alu_src2;
    
    assign result_sum = alu_src1+alu_src2_mux;
    
    //正正和负 或 负负和正 的情形
    assign ov_sum = ((!alu_src1[31] && !alu_src2_mux[31]) && result_sum[31]) ||((alu_src1[31] && alu_src2_mux[31]) && (!result_sum[31]));  
    
    // 比较结果
    wire src1_lt_src2;
    assign src1_lt_src2 = ((alu_control==`SLT_OP||alu_control==`SLTI_OP))?
        ((alu_src1[31]&&!alu_src2[31])||
        (!alu_src1[31]&&!alu_src2[31]&&result_sum[31])||
        (alu_src1[31]&&alu_src2[31]&&result_sum[31])):(alu_src1<alu_src2);
        
    always @(*) begin
        wd_o <= wd_i;
        wreg_o <= wreg_i;
        case(alu_control)
        `ADD_OP,`SUB_OP,`ADDU_OP,`ADDIU_OP,`SUBU_OP,`ADDI_OP:begin
            alu_result <= result_sum;
        end
        `SLT_OP,`SLTU_OP,`SLTI_OP,`SLTIU_OP:begin
            alu_result <= src1_lt_src2;
        end
        `AND_OP,`ANDI_OP:begin
            alu_result <= alu_src1 & alu_src2;
        end
        `NOR_OP:begin
            alu_result <= ~(alu_src1|alu_src2);
        end
        `OR_OP,`ORI_OP:begin
            alu_result <= alu_src1 | alu_src2;
        end
        `XOR_OP,`XORI_OP:begin
            alu_result <= alu_src1 ^ alu_src2;
        end
        `SLL_OP,`SLLV_OP:begin
            alu_result <= alu_src2 << alu_src1[4:0];
        end
        `SRL_OP,`SRLV_OP:begin
            alu_result <= alu_src2 >> alu_src1[4:0];
        end
        `SRA_OP,`SRAV_OP:begin
            alu_result <= ({32{alu_src2[31]}} << (6'd32-{1'b0,alu_src1[4:0]}))
            | alu_src2 >> alu_src1[4:0];
        end
        `LUI_OP:begin
            alu_result <= {alu_src2[15:0],16'd0};
        end
        `JR_OP,`JAL_OP,`BEQ_OP,`BNE_OP:begin
            // 把返回地址写入寄存器
            alu_result <= link_address_i;
        end
        `MFHI_OP,`MFLO_OP,`MTHI_OP,`MTLO_OP:begin
            alu_result <= moveres;
        end
        default:begin
            alu_result <= 32'b0;
        end
    endcase
end

// always @ (*) begin
// 		if(rst == `RstEnable) begin
// 			stallreq_for_div <= `NoStop;
// 	        div_opdata1_o <= `ZeroWord;
// 			div_opdata2_o <= `ZeroWord;
// 			div_start_o <= `DivStop;
// 			signed_div_o <= 1'b0;
// 			mul_opdata1_o <=`ZeroWord;
// 			mul_opdata2_o <= `ZeroWord;
// 			mul_start_o <=1'b0;
// 			signed_mul_o <=1'b0;
// 		end else begin
// 			stallreq_for_div <= `NoStop;
// 	        div_opdata1_o <= `ZeroWord;
// 			div_opdata2_o <= `ZeroWord;
// 			div_start_o <= `DivStop;
// 			signed_div_o <= 1'b0;	
// 			mul_opdata1_o <=`ZeroWord;
//             mul_opdata2_o <= `ZeroWord;
//             mul_start_o <=1'b0;
//             signed_mul_o <=1'b0;
// 			case (alu_control) 
// 				`DIV_OP: begin
// 					if(div_ready_i == `DivResultNotReady) begin
// 	    			    div_opdata1_o <= alu_src1;
// 						div_opdata2_o <= alu_src2;
// 						div_start_o <= `DivStart;
// 						signed_div_o <= 1'b1;
// 						stallreq_for_div <= `Stop;
// 					end else if(div_ready_i == `DivResultReady) begin
// 	    			div_opdata1_o <= alu_src1;
// 						div_opdata2_o <= alu_src2;
// 						div_start_o <= `DivStop;
// 						signed_div_o <= 1'b1;
// 						stallreq_for_div <= `NoStop;
// 					end else begin						
// 	    			div_opdata1_o <= `ZeroWord;
// 						div_opdata2_o <= `ZeroWord;
// 						div_start_o <= `DivStop;
// 						signed_div_o <= 1'b0;
// 						stallreq_for_div <= `NoStop;
// 					end					
// 				end
// 				`DIVU_OP:begin
// 					if(div_ready_i == `DivResultNotReady) begin
// 	    			div_opdata1_o <= alu_src1;
// 						div_opdata2_o <= alu_src2;
// 						div_start_o <= `DivStart;
// 						signed_div_o <= 1'b0;
// 						stallreq_for_div <= `Stop;
// 					end else if(div_ready_i == `DivResultReady) begin
// 	    			    div_opdata1_o <= alu_src1;
// 						div_opdata2_o <=alu_src2;
// 						div_start_o <= `DivStop;
// 						signed_div_o <= 1'b0;
// 						stallreq_for_div <= `NoStop;
// 					end else begin						
// 	    			    div_opdata1_o <= `ZeroWord;
// 						div_opdata2_o <= `ZeroWord;
// 						div_start_o <= `DivStop;
// 						signed_div_o <= 1'b0;
// 						stallreq_for_div <= `NoStop;
// 					end					
// 				end
// 				`MULT_OP:begin
// 				    if(mul_ready_i==`MulResultNotReady) begin
//                         mul_start_o <= `MulStart;
//                         mul_opdata1_o <= alu_src1;
//                         mul_opdata2_o <= alu_src2;
//                         signed_mul_o <=1'b1;
//                         stallreq_for_mul<=`Stop;
// 				   end else if(mul_ready_i==`MulResultReady) begin
// 				       mul_start_o <= `MulStop;
//                        mul_opdata1_o <= alu_src1;
//                        mul_opdata2_o <= alu_src2;
//                        signed_mul_o <=1'b0;
//                        stallreq_for_mul<=`NoStop;
// 				   end else begin
// 				        mul_opdata1_o <= `ZeroWord;
// 				        mul_opdata2_o <= `ZeroWord;
// 				        mul_start_o <= 1'b0;
// 				        signed_mul_o <= 1'b0;
//                         stallreq_for_mul <= `NoStop;
//                     end
//                 end
// 				default: begin
// 				end
// 			endcase
// 		end
// 	end	
	
	// // 暂停流水线
	// always @(*) begin
	//    stallreq = stallreq_for_div || stallreq_for_mul;
    // end
    
    // reg [63:0] mul_result;
    
    // // HI LO 流水线部分
    // always @ (*) begin
    //         if(rst == `RstEnable) begin
    //             whilo_o <= `WriteDisable;
    //             lo_o <= `ZeroWord;
    //             hi_o <= `ZeroWord;
    //         // ------ 乘法和除法 ------
    //         end else if((alu_control == `DIV_OP) || (alu_control == `DIVU_OP)) begin
    //             whilo_o <= `WriteEnable;
    //             hi_o <= div_result_i[63:32];
    //             lo_o <= div_result_i[31:0];  
    //        end else if (alu_control == `MULT_OP) begin
    //             whilo_o <= `WriteEnable;
    //             hi_o <= mul_result_i[63:32];
    //             lo_o <= mul_result_i[31:0]; 
    //         end else if(alu_control == `MULTU_OP) begin
    //             whilo_o <= `WriteEnable;
    //             mul_result = alu_src1 * alu_src2;
    //             hi_o <= mul_result[63:32];
    //             lo_o <= mul_result[31:0];
    //         //------- 移动相关指令 -------
    //        end else if(alu_control == `MTHI_OP) begin
    //             whilo_o <= `WriteEnable;
    //             // MTHI 是 rs 写入 HI，因此 LO 不变传送出去，而 HI 变成 RS
    //             hi_o <= alu_src1;
    //             lo_o <= LO;
    //         end else if(alu_control == `MTLO_OP) begin
    //             // MTLO 是 rs 写入 LO，因此 HI 不变传送出去，而 LO 变成 RS
    //             whilo_o <= `WriteEnable;
    //             hi_o <= HI;
    //             lo_o <= alu_src1;
    //         end else begin
    //             whilo_o <= `WriteDisable;
    //             hi_o <= `ZeroWord;
    //             lo_o <= `ZeroWord;
    //         end
    //    end
    
    
        
endmodule
